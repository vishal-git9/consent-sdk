package com.niceoneconsent.sdk.android

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.niceoneconsent.sdk.analytics.ConsentAnalytics
import com.niceoneconsent.sdk.analytics.ConsentEventListener
import com.niceoneconsent.sdk.manager.ConsentManager
import com.niceoneconsent.sdk.models.*

/**
 * Public API for the Consent SDK on Android.
 *
 * Usage:
 * ```kotlin
 * ConsentSDK.initialize(ConsentConfig(
 *     apiKey = "your-api-key",
 *     baseUrl = "https://api.example.com",
 *     language = "en",
 *     theme = ConsentTheme.Light
 * ))
 *
 * ConsentSDK.showConsent(activity)
 * ```
 */
object ConsentSDK {

    internal val manager = ConsentManager()

    /**
     * Initialize the SDK with the given configuration.
     * Must be called before any other SDK methods.
     *
     * @param config SDK configuration including API key, base URL, language, and theme
     */
    fun initialize(config: ConsentConfig) {
        manager.initialize(config)
    }

    /**
     * Show the consent popup.
     *
     * @param activity The activity context from which to launch the popup
     * @return [ConsentResult] indicating success or an error
     */
    fun showConsent(activity: Activity): ConsentResult<Unit> {
        val result = manager.showConsent()
        if (result is ConsentResult.Success) {
            val intent = Intent(activity, ConsentActivity::class.java)
            activity.startActivity(intent)
            // Disable default transition for overlay effect
            activity.overridePendingTransition(android.R.anim.fade_in, 0)
        }
        return result
    }

    /**
     * Hide the consent popup.
     */
    fun hideConsent() {
        manager.hideConsent()
    }

    /**
     * Switch the SDK language. Purposes will be refetched automatically.
     *
     * @param langCode Language code (e.g., "en", "fr", "de")
     */
    fun setLanguage(langCode: String) {
        manager.setLanguage(langCode)
    }

    /**
     * Get the current consent submission status.
     *
     * @return true if consent has been submitted in this session
     */
    fun getConsentStatus(): Boolean {
        return manager.getConsentStatus()
    }

    /**
     * Reset all consent state to defaults.
     */
    fun resetConsent() {
        manager.resetConsent()
    }

    /**
     * Add an analytics event listener.
     */
    fun addEventListener(listener: ConsentEventListener) {
        manager.analytics.addListener(listener)
    }

    /**
     * Remove an analytics event listener.
     */
    fun removeEventListener(listener: ConsentEventListener) {
        manager.analytics.removeListener(listener)
    }

    /**
     * Release all SDK resources. Call when the SDK is no longer needed.
     */
    fun destroy() {
        manager.destroy()
    }
}
