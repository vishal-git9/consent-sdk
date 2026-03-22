import Foundation
import SwiftUI

/// ViewModel bridging the API client and business logic to SwiftUI views.
///
/// This is an `ObservableObject` that publishes state changes for reactive UI updates.
@MainActor
public final class ConsentViewModel: ObservableObject {

    @Published public private(set) var state = ConsentState()

    private var config: ConsentConfig?
    private var apiClient: ConsentApiClient?
    private var isSubmissionInProgress = false

    let analytics = ConsentAnalytics()

    // MARK: - Initialization

    func configure(with config: ConsentConfig) {
        self.config = config
        self.apiClient = ConsentApiClient(
            baseUrl: config.baseUrl,
            apiKey: config.apiKey,
            timeoutInterval: config.timeoutInterval
        )
        state = ConsentState(language: config.language)
    }

    var isInitialized: Bool { config != nil }

    // MARK: - Popup Lifecycle

    func showConsent() -> ConsentResult<Void> {
        guard config != nil else {
            return .error(message: "SDK not initialized. Call initialize() first.", code: .notInitialized)
        }
        guard !state.isPopupVisible else {
            return .error(message: "Consent popup is already visible.", code: .alreadyVisible)
        }

        state.isPopupVisible = true
        state.error = nil
        state.validationErrors = [:]
        state.isConsentSubmitted = false

        analytics.dispatch(.consentShown(language: state.language))
        fetchPurposes()

        return .success(())
    }

    func hideConsent() {
        state.isPopupVisible = false
        analytics.dispatch(.consentDismissed)
    }

    // MARK: - Data Operations

    func fetchPurposes() {
        guard let apiClient = apiClient else { return }
        let lang = state.language
        let retryCount = config?.retryCount ?? 2

        state.isLoading = true
        state.error = nil

        Task {
            var lastResult: ConsentResult<[Purpose]>?

            for attempt in 0...retryCount {
                lastResult = await apiClient.fetchPurposes(language: lang)
                if case .success = lastResult { break }
                if attempt < retryCount {
                    try? await Task.sleep(nanoseconds: UInt64(1_000_000_000 * (attempt + 1)))
                }
            }

            switch lastResult {
            case .success(let purposes):
                let mandatoryIds = Set(purposes.filter { $0.mandatory }.map { $0.id })
                state.purposes = purposes
                state.selectedPurposes = state.selectedPurposes.union(mandatoryIds)
                state.isLoading = false
                state.error = nil

            case .error(let message, let code):
                state.isLoading = false
                state.error = message
                analytics.dispatch(.error(message: message, code: code.rawValue))

            case .none:
                state.isLoading = false
                state.error = "Unknown error occurred"
            }
        }
    }

    // MARK: - User Actions

    func setAge(_ age: String) {
        state.age = age
        state.validationErrors.removeValue(forKey: "age")
    }

    func togglePurpose(_ purposeId: String) {
        guard let purpose = state.purposes.first(where: { $0.id == purposeId }) else { return }

        // Prevent deselecting mandatory purposes
        if purpose.mandatory && state.selectedPurposes.contains(purposeId) { return }

        if state.selectedPurposes.contains(purposeId) {
            state.selectedPurposes.remove(purposeId)
        } else {
            state.selectedPurposes.insert(purposeId)
        }
    }

    // MARK: - Validation

    func validate() -> [String: String] {
        var errors: [String: String] = [:]
        let minimumAge = config?.minimumAge ?? 16

        // Age validation
        if state.age.trimmingCharacters(in: .whitespaces).isEmpty {
            errors["age"] = "Age is required"
        } else if let ageInt = Int(state.age) {
            if ageInt < 0 {
                errors["age"] = "Age cannot be negative"
            } else if ageInt < minimumAge {
                errors["age"] = "You must be at least \(minimumAge) years old"
            } else if ageInt > 150 {
                errors["age"] = "Please enter a valid age"
            }
        } else {
            errors["age"] = "Age must be a valid number"
        }

        // Mandatory purpose validation
        let missingMandatory = state.purposes
            .filter { $0.mandatory }
            .filter { !state.selectedPurposes.contains($0.id) }
        if !missingMandatory.isEmpty {
            let names = missingMandatory.map { $0.name }.joined(separator: ", ")
            errors["purposes"] = "Required purposes must be selected: \(names)"
        }

        state.validationErrors = errors
        return errors
    }

    // MARK: - Submission

    func submitConsent() {
        guard !state.isSubmitting, !isSubmissionInProgress else { return }

        let errors = validate()
        guard errors.isEmpty else { return }

        guard let apiClient = apiClient else { return }

        isSubmissionInProgress = true
        state.isSubmitting = true
        state.error = nil

        Task {
            let result = await apiClient.submitConsent(
                age: Int(state.age) ?? 0,
                selectedPurposes: Array(state.selectedPurposes),
                language: state.language
            )

            isSubmissionInProgress = false

            switch result {
            case .success:
                state.isSubmitting = false
                state.isConsentSubmitted = true
                state.isPopupVisible = false
                state.error = nil
                analytics.dispatch(.consentSubmitted(
                    selectedPurposes: Array(state.selectedPurposes),
                    age: Int(state.age) ?? 0,
                    language: state.language
                ))

            case .error(let message, let code):
                state.isSubmitting = false
                state.error = message
                analytics.dispatch(.error(message: message, code: code.rawValue))
            }
        }
    }

    func declineConsent() {
        analytics.dispatch(.consentDeclined(language: state.language))
        state.isPopupVisible = false
        state.selectedPurposes = []
        state.age = ""
        state.validationErrors = [:]
        state.error = nil
    }

    // MARK: - Language

    func setLanguage(_ langCode: String) {
        let oldLang = state.language
        guard oldLang != langCode else { return }

        state.language = langCode
        analytics.dispatch(.languageChanged(from: oldLang, to: langCode))

        if state.isPopupVisible {
            fetchPurposes()
        }
    }

    // MARK: - Status & Reset

    func getConsentStatus() -> Bool {
        return state.isConsentSubmitted
    }

    func resetConsent() {
        let lang = state.language
        state = ConsentState(language: lang)
    }

    func destroy() {
        analytics.clearListeners()
        config = nil
        apiClient = nil
        state = ConsentState()
    }
}
