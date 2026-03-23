/**
 * Theme customization for the consent popup UI.
 */
export interface ConsentTheme {
    primaryColor?: string;
    backgroundColor?: string;
    textColor?: string;
    buttonRadius?: number;
    fontFamily?: string;
    checkboxColor?: string;
    buttonTextColor?: string;
    overlayColor?: string;
    errorColor?: string;
    cardElevation?: number;
    secondaryButtonColor?: string;
    secondaryButtonTextColor?: string;
    inputBorderColor?: string;
    inputBackgroundColor?: string;
    mandatoryBadgeColor?: string;
}
/**
 * Configuration for initializing the Consent SDK.
 */
export interface ConsentConfig {
    /** API key for the consent backend. */
    apiKey: string;
    /** Base URL for the consent API (no trailing slash). */
    baseUrl: string;
    /** Initial language code (e.g., "en", "fr"). Defaults to "en". */
    language?: string;
    /** UI theme customization. */
    theme?: ConsentTheme;
    /** Minimum age for consent. Defaults to 16. */
    minimumAge?: number;
    /** Request timeout in milliseconds. Defaults to 15000. */
    timeoutMs?: number;
    /** Number of retries on failure. Defaults to 2. */
    retryCount?: number;
}
/**
 * Analytics event types emitted by the SDK.
 */
export type ConsentEventType = 'consentShown' | 'consentSubmitted' | 'consentDeclined' | 'consentDismissed' | 'languageChanged' | 'error';
/**
 * Analytics event payload.
 */
export interface ConsentEventPayload {
    type: ConsentEventType;
    data?: Record<string, any>;
}
/**
 * Event listener callback type.
 */
export type ConsentEventCallback = (event: ConsentEventPayload) => void;
declare class ConsentSDKClass {
    private eventListeners;
    /**
     * Initialize the SDK with the given configuration.
     * Must be called before any other methods.
     */
    initialize(config: ConsentConfig): Promise<void>;
    /**
     * Display the consent popup.
     */
    showConsent(): Promise<void>;
    /**
     * Hide the consent popup.
     */
    hideConsent(): Promise<void>;
    /**
     * Switch the SDK language. Purposes will be refetched automatically.
     *
     * @param langCode Language code (e.g., "en", "fr", "de")
     */
    setLanguage(langCode: string): Promise<void>;
    /**
     * Get the current consent submission status.
     *
     * @returns true if consent has been submitted in this session
     */
    getConsentStatus(): Promise<boolean>;
    /**
     * Reset all consent state to defaults.
     */
    resetConsent(): Promise<void>;
    /**
     * Add an event listener for consent analytics events.
     */
    addEventListener(callback: ConsentEventCallback): () => void;
    /**
     * Remove all event listeners.
     */
    removeAllEventListeners(): void;
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
declare const ConsentSDK: ConsentSDKClass;
export default ConsentSDK;
