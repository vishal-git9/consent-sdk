import Foundation

/// Configuration object for initializing the Consent SDK.
public struct ConsentConfig {
    /// API key for authenticating with the consent backend
    public let apiKey: String
    /// Base URL for the consent API (no trailing slash)
    public let baseUrl: String
    /// Initial language code (e.g., "en", "fr", "de")
    public let language: String
    /// UI theme customization
    public let theme: ConsentTheme
    /// Minimum allowed age for consent
    public let minimumAge: Int
    /// Network request timeout in seconds
    public let timeoutInterval: TimeInterval
    /// Number of automatic retries on network failure
    public let retryCount: Int

    public init(
        apiKey: String,
        baseUrl: String,
        language: String = "en",
        theme: ConsentTheme = .light,
        minimumAge: Int = 16,
        timeoutInterval: TimeInterval = 15,
        retryCount: Int = 2
    ) {
        self.apiKey = apiKey
        self.baseUrl = baseUrl
        self.language = language
        self.theme = theme
        self.minimumAge = minimumAge
        self.timeoutInterval = timeoutInterval
        self.retryCount = retryCount
    }
}
