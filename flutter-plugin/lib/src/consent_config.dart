import 'consent_theme.dart';

/// Configuration for initializing the Consent SDK.
class ConsentConfig {
  /// API key for the consent backend.
  final String apiKey;

  /// Base URL for the consent API (no trailing slash).
  final String baseUrl;

  /// Initial language code (e.g., "en", "fr"). Defaults to "en".
  final String language;

  /// UI theme customization. Defaults to [ConsentTheme.light].
  final ConsentTheme theme;

  /// Minimum age for consent. Defaults to 16.
  final int minimumAge;

  /// Request timeout in milliseconds. Defaults to 15000.
  final int timeoutMs;

  /// Number of retries on failure. Defaults to 2.
  final int retryCount;

  const ConsentConfig({
    required this.apiKey,
    required this.baseUrl,
    this.language = 'en',
    this.theme = ConsentTheme.light,
    this.minimumAge = 16,
    this.timeoutMs = 15000,
    this.retryCount = 2,
  });

  /// Convert to a map for platform channel serialization.
  Map<String, dynamic> toMap() {
    return {
      'apiKey': apiKey,
      'baseUrl': baseUrl,
      'language': language,
      'theme': theme.toMap(),
      'minimumAge': minimumAge,
      'timeoutMs': timeoutMs,
      'retryCount': retryCount,
    };
  }
}
