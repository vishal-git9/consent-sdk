"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const react_native_1 = require("react-native");
const LINKING_ERROR = `The package 'react-native-consent-sdk' doesn't seem to be linked. Make sure: \n\n` +
    react_native_1.Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
    '- You rebuilt the app after installing the package\n' +
    '- You are not using Expo Go\n';
const ConsentNativeModule = react_native_1.NativeModules.ConsentSDKModule
    ? react_native_1.NativeModules.ConsentSDKModule
    : new Proxy({}, {
        get() {
            throw new Error(LINKING_ERROR);
        },
    });
// ─── SDK ──────────────────────────────────────────────────────────
class ConsentSDKClass {
    constructor() {
        this.eventListeners = [];
    }
    /**
     * Initialize the SDK with the given configuration.
     * Must be called before any other methods.
     */
    async initialize(config) {
        return ConsentNativeModule.initialize({
            apiKey: config.apiKey,
            baseUrl: config.baseUrl,
            language: config.language ?? 'en',
            theme: config.theme ?? {},
            minimumAge: config.minimumAge ?? 16,
            timeoutMs: config.timeoutMs ?? 15000,
            retryCount: config.retryCount ?? 2,
        });
    }
    /**
     * Display the consent popup.
     */
    async showConsent() {
        return ConsentNativeModule.showConsent();
    }
    /**
     * Hide the consent popup.
     */
    async hideConsent() {
        return ConsentNativeModule.hideConsent();
    }
    /**
     * Switch the SDK language. Purposes will be refetched automatically.
     *
     * @param langCode Language code (e.g., "en", "fr", "de")
     */
    async setLanguage(langCode) {
        return ConsentNativeModule.setLanguage(langCode);
    }
    /**
     * Get the current consent submission status.
     *
     * @returns true if consent has been submitted in this session
     */
    async getConsentStatus() {
        return ConsentNativeModule.getConsentStatus();
    }
    /**
     * Reset all consent state to defaults.
     */
    async resetConsent() {
        return ConsentNativeModule.resetConsent();
    }
    /**
     * Add an event listener for consent analytics events.
     */
    addEventListener(callback) {
        this.eventListeners.push(callback);
        return () => {
            this.eventListeners = this.eventListeners.filter((cb) => cb !== callback);
        };
    }
    /**
     * Remove all event listeners.
     */
    removeAllEventListeners() {
        this.eventListeners = [];
    }
}
/**
 * Consent SDK singleton.
 *
 * @example
 * ```typescript
 * import ConsentSDK from 'react-native-consent-sdk';
 *
 * // Initialize
 * await ConsentSDK.initialize({
 *   apiKey: 'your-api-key',
 *   baseUrl: 'https://api.example.com',
 *   language: 'en',
 * });
 *
 * // Show popup
 * await ConsentSDK.showConsent();
 *
 * // Switch language
 * await ConsentSDK.setLanguage('fr');
 * ```
 */
const ConsentSDK = new ConsentSDKClass();
exports.default = ConsentSDK;
