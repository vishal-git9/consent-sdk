import SwiftUI

/// Public API for the Consent SDK on iOS.
///
/// Usage:
/// ```swift
/// ConsentSDK.shared.initialize(config: ConsentConfig(
///     apiKey: "your-api-key",
///     baseUrl: "https://api.example.com",
///     language: "en",
///     theme: .light
/// ))
///
/// ConsentSDK.shared.showConsent(from: viewController)
/// ```
@MainActor
public final class ConsentSDK {

    /// Shared singleton instance.
    public static let shared = ConsentSDK()

    internal let viewModel = ConsentViewModel()

    private init() {}

    // MARK: - Public API

    /// Initialize the SDK with the given configuration.
    /// Must be called before any other SDK methods.
    public func initialize(config: ConsentConfig) {
        viewModel.configure(with: config)
    }

    /// Show the consent popup from a view controller.
    ///
    /// - Parameter viewController: The presenting view controller
    /// - Returns: Result indicating success or error
    @discardableResult
    public func showConsent(from viewController: UIViewController? = nil) -> ConsentResult<Void> {
        let result = viewModel.showConsent()

        if case .success = result {
            let popupVC = ConsentPopupHostController(viewModel: viewModel)
            popupVC.modalPresentationStyle = .overFullScreen
            popupVC.modalTransitionStyle = .crossDissolve

            if let vc = viewController ?? Self.topViewController() {
                vc.present(popupVC, animated: true)
            }
        }

        return result
    }

    /// Hide the consent popup.
    public func hideConsent() {
        viewModel.hideConsent()
    }

    /// Switch the SDK language. Purposes will be refetched automatically.
    public func setLanguage(_ langCode: String) {
        viewModel.setLanguage(langCode)
    }

    /// Get the current consent submission status.
    public func getConsentStatus() -> Bool {
        return viewModel.getConsentStatus()
    }

    /// Reset all consent state to defaults.
    public func resetConsent() {
        viewModel.resetConsent()
    }

    /// Add an analytics event listener.
    public func addEventListener(_ listener: ConsentEventListener) {
        viewModel.analytics.addListener(listener)
    }

    /// Remove an analytics event listener.
    public func removeEventListener(_ listener: ConsentEventListener) {
        viewModel.analytics.removeListener(listener)
    }

    /// Release all SDK resources.
    public func destroy() {
        viewModel.destroy()
    }

    // MARK: - Helpers

    /// Find the topmost view controller in the hierarchy.
    private static func topViewController(
        base: UIViewController? = nil
    ) -> UIViewController? {
        let base = base ?? UIApplication.shared.connectedScenes
            .compactMap { $0 as? UIWindowScene }
            .flatMap { $0.windows }
            .first(where: { $0.isKeyWindow })?.rootViewController

        if let nav = base as? UINavigationController {
            return topViewController(base: nav.visibleViewController)
        }
        if let tab = base as? UITabBarController, let selected = tab.selectedViewController {
            return topViewController(base: selected)
        }
        if let presented = base?.presentedViewController {
            return topViewController(base: presented)
        }
        return base
    }
}

// MARK: - Hosting Controller

import UIKit

/// UIHostingController wrapper to present the SwiftUI popup.
final class ConsentPopupHostController: UIHostingController<ConsentPopupContainerView> {

    init(viewModel: ConsentViewModel) {
        let view = ConsentPopupContainerView(viewModel: viewModel)
        super.init(rootView: view)
        self.view.backgroundColor = .clear
    }

    @MainActor required dynamic init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}

/// Container view that observes the view model and manages dismissal.
struct ConsentPopupContainerView: View {
    @ObservedObject var viewModel: ConsentViewModel

    @Environment(\.presentationMode) var presentationMode

    var body: some View {
        ZStack {
            if viewModel.state.isPopupVisible {
                ConsentPopupView(
                    state: viewModel.state,
                    theme: viewModel.isInitialized ? ConsentTheme.light : .light,
                    onAgeChanged: { viewModel.setAge($0) },
                    onPurposeToggled: { viewModel.togglePurpose($0) },
                    onSubmit: { viewModel.submitConsent() },
                    onDecline: {
                        viewModel.declineConsent()
                        presentationMode.wrappedValue.dismiss()
                    },
                    onRetry: { viewModel.fetchPurposes() },
                    onDismiss: {
                        viewModel.hideConsent()
                        presentationMode.wrappedValue.dismiss()
                    }
                )
            }
        }
        .onChange(of: viewModel.state.isConsentSubmitted) { submitted in
            if submitted {
                DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
                    presentationMode.wrappedValue.dismiss()
                }
            }
        }
    }
}
