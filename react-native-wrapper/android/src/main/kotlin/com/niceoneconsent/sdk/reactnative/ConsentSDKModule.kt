package com.niceoneconsent.sdk.reactnative

import com.facebook.react.bridge.*
import com.niceoneconsent.sdk.android.ConsentSDK
import com.niceoneconsent.sdk.models.ConsentConfig
import com.niceoneconsent.sdk.models.ConsentTheme
import com.niceoneconsent.sdk.models.ConsentResult

/**
 * React Native native module for Android.
 *
 * Bridges JavaScript API calls to the Android Consent SDK.
 */
class ConsentSDKModule(reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {

    override fun getName(): String = "ConsentSDKModule"

    @ReactMethod
    fun initialize(configMap: ReadableMap, promise: Promise) {
        try {
            val theme = if (configMap.hasKey("theme")) {
                parseTheme(configMap.getMap("theme"))
            } else {
                ConsentTheme.Light
            }

            val config = ConsentConfig(
                apiKey = configMap.getString("apiKey") ?: "",
                baseUrl = configMap.getString("baseUrl") ?: "",
                language = configMap.getString("language") ?: "en",
                theme = theme,
                minimumAge = if (configMap.hasKey("minimumAge")) configMap.getInt("minimumAge") else 16,
                timeoutMs = if (configMap.hasKey("timeoutMs")) configMap.getDouble("timeoutMs").toLong() else 15000L,
                retryCount = if (configMap.hasKey("retryCount")) configMap.getInt("retryCount") else 2
            )

            ConsentSDK.initialize(config)
            promise.resolve(null)
        } catch (e: Exception) {
            promise.reject("INIT_ERROR", "Failed to initialize SDK: ${e.message}", e)
        }
    }

    @ReactMethod
    fun showConsent(promise: Promise) {
        val activity = getCurrentActivity()
        if (activity == null) {
            promise.reject("NO_ACTIVITY", "No current activity available")
            return
        }

        try {
            when (val result = ConsentSDK.showConsent(activity)) {
                is ConsentResult.Success -> promise.resolve(null)
                is ConsentResult.Error -> promise.reject(result.code.name, result.message)
            }
        } catch (e: Exception) {
            promise.reject("SHOW_ERROR", "Failed to show consent: ${e.message}", e)
        }
    }

    @ReactMethod
    fun hideConsent(promise: Promise) {
        try {
            ConsentSDK.hideConsent()
            promise.resolve(null)
        } catch (e: Exception) {
            promise.reject("HIDE_ERROR", e.message, e)
        }
    }

    @ReactMethod
    fun setLanguage(langCode: String, promise: Promise) {
        try {
            ConsentSDK.setLanguage(langCode)
            promise.resolve(null)
        } catch (e: Exception) {
            promise.reject("LANGUAGE_ERROR", e.message, e)
        }
    }

    @ReactMethod
    fun getConsentStatus(promise: Promise) {
        try {
            promise.resolve(ConsentSDK.getConsentStatus())
        } catch (e: Exception) {
            promise.reject("STATUS_ERROR", e.message, e)
        }
    }

    @ReactMethod
    fun resetConsent(promise: Promise) {
        try {
            ConsentSDK.resetConsent()
            promise.resolve(null)
        } catch (e: Exception) {
            promise.reject("RESET_ERROR", e.message, e)
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────

    private fun parseTheme(themeMap: ReadableMap?): ConsentTheme {
        if (themeMap == null) return ConsentTheme.Light

        return ConsentTheme(
            primaryColor = themeMap.getStringOrDefault("primaryColor", "#6200EE"),
            backgroundColor = themeMap.getStringOrDefault("backgroundColor", "#FFFFFF"),
            textColor = themeMap.getStringOrDefault("textColor", "#212121"),
            buttonRadius = themeMap.getFloatOrDefault("buttonRadius", 8f),
            fontFamily = themeMap.getStringOrDefault("fontFamily", "System"),
            checkboxColor = themeMap.getStringOrDefault("checkboxColor", "#6200EE"),
            buttonTextColor = themeMap.getStringOrDefault("buttonTextColor", "#FFFFFF"),
            overlayColor = themeMap.getStringOrDefault("overlayColor", "#80000000"),
            errorColor = themeMap.getStringOrDefault("errorColor", "#B00020"),
            cardElevation = themeMap.getFloatOrDefault("cardElevation", 8f),
            secondaryButtonColor = themeMap.getStringOrDefault("secondaryButtonColor", "#757575"),
            secondaryButtonTextColor = themeMap.getStringOrDefault("secondaryButtonTextColor", "#FFFFFF"),
            inputBorderColor = themeMap.getStringOrDefault("inputBorderColor", "#BDBDBD"),
            inputBackgroundColor = themeMap.getStringOrDefault("inputBackgroundColor", "#F5F5F5"),
            mandatoryBadgeColor = themeMap.getStringOrDefault("mandatoryBadgeColor", "#FF9800")
        )
    }

    private fun ReadableMap.getStringOrDefault(key: String, default: String): String {
        return if (hasKey(key)) getString(key) ?: default else default
    }

    private fun ReadableMap.getFloatOrDefault(key: String, default: Float): Float {
        return if (hasKey(key)) getDouble(key).toFloat() else default
    }
}
