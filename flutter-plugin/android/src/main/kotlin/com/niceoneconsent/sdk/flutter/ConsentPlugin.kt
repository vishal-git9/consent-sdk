package com.niceoneconsent.sdk.flutter

import android.app.Activity
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import com.niceoneconsent.sdk.android.ConsentSDK
import com.niceoneconsent.sdk.models.ConsentConfig
import com.niceoneconsent.sdk.models.ConsentTheme
import com.niceoneconsent.sdk.models.ConsentResult

/**
 * Flutter platform channel plugin for the Consent SDK on Android.
 */
class ConsentPlugin : FlutterPlugin, MethodCallHandler, ActivityAware {

    private lateinit var channel: MethodChannel
    private var activity: Activity? = null

    override fun onAttachedToEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(binding.binaryMessenger, "consent_sdk")
        channel.setMethodCallHandler(this)
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    // ActivityAware
    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
    }

    override fun onDetachedFromActivity() {
        activity = null
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        activity = binding.activity
    }

    override fun onDetachedFromActivityForConfigChanges() {
        activity = null
    }

    // MethodCallHandler
    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        when (call.method) {
            "initialize" -> handleInitialize(call, result)
            "showConsent" -> handleShowConsent(result)
            "hideConsent" -> handleHideConsent(result)
            "setLanguage" -> handleSetLanguage(call, result)
            "getConsentStatus" -> handleGetConsentStatus(result)
            "resetConsent" -> handleResetConsent(result)
            else -> result.notImplemented()
        }
    }

    private fun handleInitialize(call: MethodCall, result: Result) {
        try {
            val apiKey = call.argument<String>("apiKey") ?: ""
            val baseUrl = call.argument<String>("baseUrl") ?: ""
            val language = call.argument<String>("language") ?: "en"
            val minimumAge = call.argument<Int>("minimumAge") ?: 16
            val timeoutMs = call.argument<Int>("timeoutMs")?.toLong() ?: 15000L
            val retryCount = call.argument<Int>("retryCount") ?: 2

            val themeMap = call.argument<Map<String, Any>>("theme")
            val theme = parseTheme(themeMap)

            val config = ConsentConfig(
                apiKey = apiKey,
                baseUrl = baseUrl,
                language = language,
                theme = theme,
                minimumAge = minimumAge,
                timeoutMs = timeoutMs,
                retryCount = retryCount
            )

            ConsentSDK.initialize(config)
            result.success(null)
        } catch (e: Exception) {
            result.error("INIT_ERROR", "Failed to initialize: ${e.message}", null)
        }
    }

    private fun handleShowConsent(result: Result) {
        val currentActivity = activity
        if (currentActivity == null) {
            result.error("NO_ACTIVITY", "No activity available", null)
            return
        }

        when (val sdkResult = ConsentSDK.showConsent(currentActivity)) {
            is ConsentResult.Success -> result.success(null)
            is ConsentResult.Error -> result.error(sdkResult.code.name, sdkResult.message, null)
        }
    }

    private fun handleHideConsent(result: Result) {
        ConsentSDK.hideConsent()
        result.success(null)
    }

    private fun handleSetLanguage(call: MethodCall, result: Result) {
        val langCode = call.argument<String>("langCode") ?: "en"
        ConsentSDK.setLanguage(langCode)
        result.success(null)
    }

    private fun handleGetConsentStatus(result: Result) {
        result.success(ConsentSDK.getConsentStatus())
    }

    private fun handleResetConsent(result: Result) {
        ConsentSDK.resetConsent()
        result.success(null)
    }

    private fun parseTheme(themeMap: Map<String, Any>?): ConsentTheme {
        if (themeMap == null) return ConsentTheme.Light

        return ConsentTheme(
            primaryColor = themeMap["primaryColor"] as? String ?: "#6200EE",
            backgroundColor = themeMap["backgroundColor"] as? String ?: "#FFFFFF",
            textColor = themeMap["textColor"] as? String ?: "#212121",
            buttonRadius = (themeMap["buttonRadius"] as? Double)?.toFloat() ?: 8f,
            fontFamily = themeMap["fontFamily"] as? String ?: "System",
            checkboxColor = themeMap["checkboxColor"] as? String ?: "#6200EE",
            buttonTextColor = themeMap["buttonTextColor"] as? String ?: "#FFFFFF",
            overlayColor = themeMap["overlayColor"] as? String ?: "#80000000",
            errorColor = themeMap["errorColor"] as? String ?: "#B00020",
            cardElevation = (themeMap["cardElevation"] as? Double)?.toFloat() ?: 8f,
            secondaryButtonColor = themeMap["secondaryButtonColor"] as? String ?: "#757575",
            secondaryButtonTextColor = themeMap["secondaryButtonTextColor"] as? String ?: "#FFFFFF",
            inputBorderColor = themeMap["inputBorderColor"] as? String ?: "#BDBDBD",
            inputBackgroundColor = themeMap["inputBackgroundColor"] as? String ?: "#F5F5F5",
            mandatoryBadgeColor = themeMap["mandatoryBadgeColor"] as? String ?: "#FF9800"
        )
    }
}
