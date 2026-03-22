package com.niceoneconsent.sdk.models

/**
 * Holds the current state of the consent popup flow.
 * This is an immutable data class; create copies via [copy] to update state.
 */
data class ConsentState(
    /** The user's entered age, or null if not yet entered. */
    val age: String = "",
    /** Set of selected purpose IDs. */
    val selectedPurposes: Set<String> = emptySet(),
    /** List of purposes fetched from the backend. */
    val purposes: List<Purpose> = emptyList(),
    /** Current language code. */
    val language: String = "en",
    /** Whether the SDK is currently loading data. */
    val isLoading: Boolean = false,
    /** Whether a consent submission is in progress. */
    val isSubmitting: Boolean = false,
    /** Current error message, or null if no error. */
    val error: String? = null,
    /** Whether the popup is currently visible. */
    val isPopupVisible: Boolean = false,
    /** Whether consent was successfully submitted. */
    val isConsentSubmitted: Boolean = false,
    /** Validation errors, keyed by field name. */
    val validationErrors: Map<String, String> = emptyMap()
)
