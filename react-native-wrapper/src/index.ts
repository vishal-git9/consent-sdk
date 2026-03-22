import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-consent-sdk' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const ConsentNativeModule = NativeModules.ConsentSDKModule
  ? NativeModules.ConsentSDKModule
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

// ─── Types ────────────────────────────────────────────────────────

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
export type ConsentEventType =
  | 'consentShown'
  | 'consentSubmitted'
  | 'consentDeclined'
  | 'consentDismissed'
  | 'languageChanged'
  | 'error';

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

// ─── SDK ──────────────────────────────────────────────────────────

class ConsentSDKClass {
  private eventListeners: ConsentEventCallback[] = [];

  /**
   * Initialize the SDK with the given configuration.
   * Must be called before any other methods.
   */
  async initialize(config: ConsentConfig): Promise<void> {
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
  async showConsent(): Promise<void> {
    return ConsentNativeModule.showConsent();
  }

  /**
   * Hide the consent popup.
   */
  async hideConsent(): Promise<void> {
    return ConsentNativeModule.hideConsent();
  }

  /**
   * Switch the SDK language. Purposes will be refetched automatically.
   *
   * @param langCode Language code (e.g., "en", "fr", "de")
   */
  async setLanguage(langCode: string): Promise<void> {
    return ConsentNativeModule.setLanguage(langCode);
  }

  /**
   * Get the current consent submission status.
   *
   * @returns true if consent has been submitted in this session
   */
  async getConsentStatus(): Promise<boolean> {
    return ConsentNativeModule.getConsentStatus();
  }

  /**
   * Reset all consent state to defaults.
   */
  async resetConsent(): Promise<void> {
    return ConsentNativeModule.resetConsent();
  }

  /**
   * Add an event listener for consent analytics events.
   */
  addEventListener(callback: ConsentEventCallback): () => void {
    this.eventListeners.push(callback);
    return () => {
      this.eventListeners = this.eventListeners.filter((cb) => cb !== callback);
    };
  }

  /**
   * Remove all event listeners.
   */
  removeAllEventListeners(): void {
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
export default ConsentSDK;
