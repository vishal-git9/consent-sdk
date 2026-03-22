package com.niceoneconsent.sdk.analytics

/**
 * Event types emitted by the Consent SDK for analytics tracking.
 */
sealed class ConsentEvent {
    /** Popup was displayed to the user. */
    data class ConsentShown(val language: String) : ConsentEvent()

    /** User successfully submitted their consent. */
    data class ConsentSubmitted(
        val selectedPurposes: List<String>,
        val age: Int,
        val language: String
    ) : ConsentEvent()

    /** User declined consent. */
    data class ConsentDeclined(val language: String) : ConsentEvent()

    /** An error occurred during an SDK operation. */
    data class Error(val message: String, val code: String) : ConsentEvent()

    /** Language was switched. */
    data class LanguageChanged(val from: String, val to: String) : ConsentEvent()

    /** Popup was dismissed. */
    data object ConsentDismissed : ConsentEvent()
}

/**
 * Listener interface for receiving consent analytics events.
 *
 * Client apps implement this to track consent interactions.
 */
fun interface ConsentEventListener {
    fun onEvent(event: ConsentEvent)
}

/**
 * Manages analytics event listeners and dispatches events.
 */
class ConsentAnalytics {
    private val listeners = mutableListOf<ConsentEventListener>()

    /** Register an event listener. */
    fun addListener(listener: ConsentEventListener) {
        listeners.add(listener)
    }

    /** Remove a previously registered listener. */
    fun removeListener(listener: ConsentEventListener) {
        listeners.remove(listener)
    }

    /** Remove all listeners. */
    fun clearListeners() {
        listeners.clear()
    }

    /** Dispatch an event to all registered listeners. */
    internal fun dispatch(event: ConsentEvent) {
        val snapshot = listeners.toList()
        snapshot.forEach { it.onEvent(event) }
    }
}
