import 'package:flutter/services.dart';
import 'consent_config.dart';

/// Public API for the Consent SDK in Flutter.
///
/// Uses MethodChannel to communicate with the native Android/iOS SDKs.
///
/// ## Usage
///
/// ```dart
/// import 'package:consent_sdk/consent_sdk.dart';
///
/// // Initialize
/// await ConsentSDK.initialize(ConsentConfig(
///   apiKey: 'your-api-key',
///   baseUrl: 'https://api.example.com',
///   language: 'en',
/// ));
///
/// // Show popup
/// await ConsentSDK.showConsent();
///
/// // Switch language
/// await ConsentSDK.setLanguage('fr');
/// ```
class ConsentSDK {
  static const MethodChannel _channel = MethodChannel('consent_sdk');

  ConsentSDK._(); // Prevent instantiation

  /// Initialize the SDK with the given configuration.
  /// Must be called before any other methods.
  static Future<void> initialize(ConsentConfig config) async {
    await _channel.invokeMethod('initialize', config.toMap());
  }

  /// Display the consent popup.
  static Future<void> showConsent() async {
    await _channel.invokeMethod('showConsent');
  }

  /// Hide the consent popup.
  static Future<void> hideConsent() async {
    await _channel.invokeMethod('hideConsent');
  }

  /// Switch the SDK language. Purposes will be refetched automatically.
  ///
  /// [langCode] Language code (e.g., "en", "fr", "de")
  static Future<void> setLanguage(String langCode) async {
    await _channel.invokeMethod('setLanguage', {'langCode': langCode});
  }

  /// Get the current consent submission status.
  ///
  /// Returns `true` if consent has been submitted in this session.
  static Future<bool> getConsentStatus() async {
    final bool result = await _channel.invokeMethod('getConsentStatus');
    return result;
  }

  /// Reset all consent state to defaults.
  static Future<void> resetConsent() async {
    await _channel.invokeMethod('resetConsent');
  }
}
