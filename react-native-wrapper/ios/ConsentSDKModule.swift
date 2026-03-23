import Foundation
import React
import SwiftUI
import ConsentSDK
/// React Native native module for iOS.
///
/// Bridges JavaScript API calls to the iOS Consent SDK.
@objc(ConsentSDKModule)
class ConsentSDKModule: NSObject {

    @objc
    static func requiresMainQueueSetup() -> Bool {
        return true
    }

    @objc
    func initialize(_ config: NSDictionary, resolver resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) {
        DispatchQueue.main.async {
            guard let apiKey = config["apiKey"] as? String,
                  let baseUrl = config["baseUrl"] as? String else {
                reject("INIT_ERROR", "apiKey and baseUrl are required", nil)
                return
            }

            let language = config["language"] as? String ?? "en"
            let minimumAge = config["minimumAge"] as? Int ?? 16
            let timeoutMs = config["timeoutMs"] as? Double ?? 15000
            let retryCount = config["retryCount"] as? Int ?? 2

            // Parse theme
            var theme = ConsentTheme.light
            if let themeDict = config["theme"] as? NSDictionary {
                theme = self.parseTheme(themeDict)
            }

            let sdkConfig = ConsentConfig(
                apiKey: apiKey,
                baseUrl: baseUrl,
                language: language,
                theme: theme,
                minimumAge: minimumAge,
                timeoutInterval: timeoutMs / 1000.0,
                retryCount: retryCount
            )

            ConsentSDK.shared.initialize(config: sdkConfig)
            resolve(nil)
        }
    }

    @objc
    func showConsent(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) {
        DispatchQueue.main.async {
            let result = ConsentSDK.shared.showConsent()
            switch result {
            case .success:
                resolve(nil)
            case .error(let message, let code):
                reject(code.rawValue, message, nil)
            }
        }
    }

    @objc
    func hideConsent(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) {
        DispatchQueue.main.async {
            ConsentSDK.shared.hideConsent()
            resolve(nil)
        }
    }

    @objc
    func setLanguage(_ langCode: String, resolver resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) {
        DispatchQueue.main.async {
            ConsentSDK.shared.setLanguage(langCode)
            resolve(nil)
        }
    }

    @objc
    func getConsentStatus(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) {
        DispatchQueue.main.async {
            resolve(ConsentSDK.shared.getConsentStatus())
        }
    }

    @objc
    func resetConsent(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) {
        DispatchQueue.main.async {
            ConsentSDK.shared.resetConsent()
            resolve(nil)
        }
    }

    // MARK: - Helpers

    private func parseTheme(_ dict: NSDictionary) -> ConsentTheme {
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
