import Flutter
import UIKit

/// Flutter platform channel plugin for the Consent SDK on iOS.
public class ConsentPlugin: NSObject, FlutterPlugin {

    public static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(
            name: "consent_sdk",
            binaryMessenger: registrar.messenger()
        )
        let instance = ConsentPlugin()
        registrar.addMethodCallDelegate(instance, channel: channel)
    }

    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        switch call.method {
        case "initialize":
            handleInitialize(call, result: result)
        case "showConsent":
            handleShowConsent(result: result)
        case "hideConsent":
            handleHideConsent(result: result)
        case "setLanguage":
            handleSetLanguage(call, result: result)
        case "getConsentStatus":
            handleGetConsentStatus(result: result)
        case "resetConsent":
            handleResetConsent(result: result)
        default:
            result(FlutterMethodNotImplemented)
        }
    }

    private func handleInitialize(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        guard let args = call.arguments as? [String: Any],
              let apiKey = args["apiKey"] as? String,
              let baseUrl = args["baseUrl"] as? String else {
            result(FlutterError(code: "INIT_ERROR", message: "apiKey and baseUrl are required", details: nil))
            return
        }

        let language = args["language"] as? String ?? "en"
        let minimumAge = args["minimumAge"] as? Int ?? 16
        let timeoutMs = args["timeoutMs"] as? Double ?? 15000
        let retryCount = args["retryCount"] as? Int ?? 2

        var theme = ConsentTheme.light
        if let themeDict = args["theme"] as? [String: Any] {
            theme = parseTheme(themeDict)
        }

        let config = ConsentConfig(
            apiKey: apiKey,
            baseUrl: baseUrl,
            language: language,
            theme: theme,
            minimumAge: minimumAge,
            timeoutInterval: timeoutMs / 1000.0,
            retryCount: retryCount
        )

        DispatchQueue.main.async {
            ConsentSDK.shared.initialize(config: config)
            result(nil)
        }
    }

    private func handleShowConsent(result: @escaping FlutterResult) {
        DispatchQueue.main.async {
            let sdkResult = ConsentSDK.shared.showConsent()
            switch sdkResult {
            case .success:
                result(nil)
            case .error(let message, let code):
                result(FlutterError(code: code.rawValue, message: message, details: nil))
            }
        }
    }

    private func handleHideConsent(result: @escaping FlutterResult) {
        DispatchQueue.main.async {
            ConsentSDK.shared.hideConsent()
            result(nil)
        }
    }

    private func handleSetLanguage(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        guard let args = call.arguments as? [String: Any],
              let langCode = args["langCode"] as? String else {
            result(FlutterError(code: "LANG_ERROR", message: "langCode is required", details: nil))
            return
        }

        DispatchQueue.main.async {
            ConsentSDK.shared.setLanguage(langCode)
            result(nil)
        }
    }

    private func handleGetConsentStatus(result: @escaping FlutterResult) {
        DispatchQueue.main.async {
            result(ConsentSDK.shared.getConsentStatus())
        }
    }

    private func handleResetConsent(result: @escaping FlutterResult) {
        DispatchQueue.main.async {
            ConsentSDK.shared.resetConsent()
            result(nil)
        }
    }

    private func parseTheme(_ dict: [String: Any]) -> ConsentTheme {
        return ConsentTheme(
            primaryColor: Color(hex: dict["primaryColor"] as? String ?? "#6200EE"),
            backgroundColor: Color(hex: dict["backgroundColor"] as? String ?? "#FFFFFF"),
            textColor: Color(hex: dict["textColor"] as? String ?? "#212121"),
            buttonRadius: CGFloat(dict["buttonRadius"] as? Double ?? 8),
            fontFamily: dict["fontFamily"] as? String ?? "System",
            checkboxColor: Color(hex: dict["checkboxColor"] as? String ?? "#6200EE"),
            buttonTextColor: Color(hex: dict["buttonTextColor"] as? String ?? "#FFFFFF"),
            overlayColor: Color(hex: dict["overlayColor"] as? String ?? "#80000000"),
            errorColor: Color(hex: dict["errorColor"] as? String ?? "#B00020")
        )
    }
}
