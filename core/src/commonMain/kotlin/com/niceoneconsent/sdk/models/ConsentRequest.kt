package com.niceoneconsent.sdk.models

import kotlinx.serialization.Serializable

/**
 * Request body for submitting user consent to the backend.
 */
@Serializable
data class ConsentRequest(
    val age: Int,
    val selectedPurposes: List<String>,
    val language: String,
    val timestamp: Long
)

/**
 * Response from the consent submission API.
 */
@Serializable
data class ConsentResponse(
    val success: Boolean,
    val message: String? = null
)
