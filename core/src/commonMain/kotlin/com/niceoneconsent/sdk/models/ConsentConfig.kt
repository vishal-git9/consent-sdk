package com.niceoneconsent.sdk.models

/**
 * Configuration object for initializing the Consent SDK.
 *
 * @property apiKey API key for authenticating with the consent backend
 * @property baseUrl Base URL for the consent API (no trailing slash)
 * @property language Initial language code (e.g., "en", "fr", "de")
 * @property theme UI theme customization; defaults to [ConsentTheme.Light]
 * @property minimumAge Minimum allowed age for consent; defaults to 16
 * @property timeoutMs Network request timeout in milliseconds; defaults to 15000
 * @property retryCount Number of automatic retries on network failure; defaults to 2
 */
data class ConsentConfig(
    val apiKey: String,
    val baseUrl: String,
    val language: String = "en",
    val theme: ConsentTheme = ConsentTheme.Light,
    val minimumAge: Int = 16,
    val timeoutMs: Long = 15000,
    val retryCount: Int = 2
)
