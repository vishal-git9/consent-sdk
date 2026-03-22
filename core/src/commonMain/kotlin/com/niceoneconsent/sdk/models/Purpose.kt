package com.niceoneconsent.sdk.models

import kotlinx.serialization.Serializable

/**
 * Represents a single consent purpose fetched from the backend.
 *
 * @property id Unique identifier for the purpose (e.g., "sales", "marketing")
 * @property name Display name of the purpose
 * @property description Human-readable description of what this purpose entails
 * @property mandatory Whether this purpose is required and cannot be deselected
 */
@Serializable
data class Purpose(
    val id: String,
    val name: String,
    val description: String,
    val mandatory: Boolean = false
)

/**
 * Wrapper for the purposes API response.
 */
@Serializable
data class PurposesResponse(
    val purposes: List<Purpose>
)
