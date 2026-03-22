import Foundation

/// Holds the current state of the consent popup flow.
public struct ConsentState {
    public var age: String = ""
    public var selectedPurposes: Set<String> = []
    public var purposes: [Purpose] = []
    public var language: String = "en"
    public var isLoading: Bool = false
    public var isSubmitting: Bool = false
    public var error: String? = nil
    public var isPopupVisible: Bool = false
    public var isConsentSubmitted: Bool = false
    public var validationErrors: [String: String] = [:]
}

/// Error codes for consent operations.
public enum ConsentErrorCode: String {
    case networkError = "NETWORK_ERROR"
    case timeout = "TIMEOUT"
    case serverError = "SERVER_ERROR"
    case validationError = "VALIDATION_ERROR"
    case notInitialized = "NOT_INITIALIZED"
    case alreadyVisible = "ALREADY_VISIBLE"
    case unknown = "UNKNOWN"
}

/// Result type for consent operations.
public enum ConsentResult<T> {
    case success(T)
    case error(message: String, code: ConsentErrorCode)
}
