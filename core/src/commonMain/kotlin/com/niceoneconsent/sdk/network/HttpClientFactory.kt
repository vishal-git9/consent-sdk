package com.niceoneconsent.sdk.network

import io.ktor.client.*

/**
 * Factory for creating platform-specific HTTP client engines.
 * Uses expect/actual pattern for KMP.
 */
expect fun createHttpClient(): HttpClient
