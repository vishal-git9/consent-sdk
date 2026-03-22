package com.niceoneconsent.sdk.manager

import com.niceoneconsent.sdk.analytics.ConsentAnalytics
import com.niceoneconsent.sdk.analytics.ConsentEvent
import com.niceoneconsent.sdk.models.*
import com.niceoneconsent.sdk.network.ConsentApi
import com.niceoneconsent.sdk.network.createHttpClient
import com.niceoneconsent.sdk.util.currentTimeMillis
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Core state manager for the Consent SDK.
 *
 * Manages the full consent lifecycle: initialization, fetching purposes,
 * validation, submission, and language switching.
 *
 * This class is the single source of truth for consent state, exposed as a
 * [StateFlow] for reactive UI binding.
 */
class ConsentManager {

    private var config: ConsentConfig? = null
    private var api: ConsentApi? = null
    private var scope: CoroutineScope? = null
    private var submissionJob: Job? = null

    private val _state = MutableStateFlow(ConsentState())
    /** Observable consent state. UI layers should collect this flow. */
    val state: StateFlow<ConsentState> = _state.asStateFlow()

    /** Analytics event dispatcher. */
    val analytics = ConsentAnalytics()

    /** Whether the SDK has been initialized. */
    val isInitialized: Boolean get() = config != null

    /** Current configuration, or null if not initialized. */
    val currentConfig: ConsentConfig? get() = config

    // ─── Initialization ───────────────────────────────────────────

    /**
     * Initialize the SDK with the given configuration.
     *
     * Must be called before any other SDK methods.
     * Safe to call multiple times; will reconfigure on each call.
     */
    fun initialize(consentConfig: ConsentConfig) {
        // Clean up previous session
        scope?.cancel()
        api?.close()

        config = consentConfig
        api = ConsentApi(
            client = createHttpClient(),
            baseUrl = consentConfig.baseUrl,
            apiKey = consentConfig.apiKey
        )
        scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

        _state.value = ConsentState(language = consentConfig.language)
    }

    // ─── Popup Lifecycle ──────────────────────────────────────────

    /**
     * Show the consent popup.
     *
     * Automatically fetches purposes for the current language.
     * If the popup is already visible, this is a no-op.
     *
     * @return [ConsentResult.Error] if SDK is not initialized or popup is already visible
     */
    fun showConsent(): ConsentResult<Unit> {
        val cfg = config ?: return ConsentResult.Error(
            message = "SDK not initialized. Call initialize() first.",
            code = ErrorCode.NOT_INITIALIZED
        )

        if (_state.value.isPopupVisible) {
            return ConsentResult.Error(
                message = "Consent popup is already visible.",
                code = ErrorCode.ALREADY_VISIBLE
            )
        }

        _state.update { it.copy(
            isPopupVisible = true,
            error = null,
            validationErrors = emptyMap(),
            isConsentSubmitted = false
        )}

        analytics.dispatch(ConsentEvent.ConsentShown(_state.value.language))
        fetchPurposes()

        return ConsentResult.Success(Unit)
    }

    /**
     * Hide the consent popup.
     */
    fun hideConsent() {
        _state.update { it.copy(isPopupVisible = false) }
        analytics.dispatch(ConsentEvent.ConsentDismissed)
    }

    // ─── Data Operations ──────────────────────────────────────────

    /**
     * Fetch purposes from the backend for the current language.
     * Updates state with loading/error/success accordingly.
     */
    fun fetchPurposes() {
        val currentApi = api ?: return
        val lang = _state.value.language

        scope?.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val retryCount = config?.retryCount ?: 2
            var lastResult: ConsentResult<PurposesResponse>? = null

            for (attempt in 0..retryCount) {
                lastResult = currentApi.fetchPurposes(lang)
                if (lastResult is ConsentResult.Success) break
                if (attempt < retryCount) delay(1000L * (attempt + 1)) // Exponential backoff
            }

            when (lastResult) {
                is ConsentResult.Success -> {
                    val purposes = lastResult.data.purposes
                    // Auto-select mandatory purposes
                    val mandatoryIds = purposes.filter { it.mandatory }.map { it.id }.toSet()
                    val currentSelected = _state.value.selectedPurposes

                    _state.update { it.copy(
                        purposes = purposes,
                        selectedPurposes = currentSelected + mandatoryIds,
                        isLoading = false,
                        error = null
                    )}
                }
                is ConsentResult.Error -> {
                    _state.update { it.copy(
                        isLoading = false,
                        error = lastResult.message
                    )}
                    analytics.dispatch(ConsentEvent.Error(lastResult.message, lastResult.code.name))
                }
                null -> {
                    _state.update { it.copy(
                        isLoading = false,
                        error = "Unknown error occurred"
                    )}
                }
            }
        }
    }

    // ─── User Actions ─────────────────────────────────────────────

    /**
     * Update the age input value.
     */
    fun setAge(age: String) {
        _state.update { it.copy(
            age = age,
            validationErrors = it.validationErrors - "age"
        )}
    }

    /**
     * Toggle a purpose's selection state.
     * Mandatory purposes cannot be deselected.
     */
    fun togglePurpose(purposeId: String) {
        val currentState = _state.value
        val purpose = currentState.purposes.find { it.id == purposeId } ?: return

        // Prevent deselecting mandatory purposes
        if (purpose.mandatory && purposeId in currentState.selectedPurposes) return

        val newSelected = if (purposeId in currentState.selectedPurposes) {
            currentState.selectedPurposes - purposeId
        } else {
            currentState.selectedPurposes + purposeId
        }

        _state.update { it.copy(selectedPurposes = newSelected) }
    }

    // ─── Validation ───────────────────────────────────────────────

    /**
     * Validate the current consent form state.
     *
     * @return Map of field names to error messages; empty if valid
     */
    fun validate(): Map<String, String> {
        val currentState = _state.value
        val errors = mutableMapOf<String, String>()
        val minimumAge = config?.minimumAge ?: 16

        // Age validation
        when {
            currentState.age.isBlank() -> {
                errors["age"] = "Age is required"
            }
            currentState.age.toIntOrNull() == null -> {
                errors["age"] = "Age must be a valid number"
            }
            (currentState.age.toIntOrNull() ?: 0) < 0 -> {
                errors["age"] = "Age cannot be negative"
            }
            (currentState.age.toIntOrNull() ?: 0) < minimumAge -> {
                errors["age"] = "You must be at least $minimumAge years old"
            }
            (currentState.age.toIntOrNull() ?: 0) > 150 -> {
                errors["age"] = "Please enter a valid age"
            }
        }

        // Mandatory purpose validation
        val mandatoryPurposes = currentState.purposes.filter { it.mandatory }
        val missingMandatory = mandatoryPurposes.filter { it.id !in currentState.selectedPurposes }
        if (missingMandatory.isNotEmpty()) {
            errors["purposes"] = "Required purposes must be selected: ${missingMandatory.joinToString { it.name }}"
        }

        _state.update { it.copy(validationErrors = errors) }
        return errors
    }

    // ─── Submission ───────────────────────────────────────────────

    /**
     * Submit the user's consent.
     *
     * Validates input first, then sends to backend.
     * Prevents duplicate submissions from double-clicks.
     */
    fun submitConsent() {
        // Prevent duplicate submissions
        if (_state.value.isSubmitting) return
        if (submissionJob?.isActive == true) return

        val validationErrors = validate()
        if (validationErrors.isNotEmpty()) return

        val currentApi = api ?: return
        val currentState = _state.value

        val request = ConsentRequest(
            age = currentState.age.toInt(),
            selectedPurposes = currentState.selectedPurposes.toList(),
            language = currentState.language,
            timestamp = currentTimeMillis()
        )

        submissionJob = scope?.launch {
            _state.update { it.copy(isSubmitting = true, error = null) }

            when (val result = currentApi.submitConsent(request)) {
                is ConsentResult.Success -> {
                    _state.update { it.copy(
                        isSubmitting = false,
                        isConsentSubmitted = true,
                        isPopupVisible = false,
                        error = null
                    )}
                    analytics.dispatch(ConsentEvent.ConsentSubmitted(
                        selectedPurposes = currentState.selectedPurposes.toList(),
                        age = currentState.age.toInt(),
                        language = currentState.language
                    ))
                }
                is ConsentResult.Error -> {
                    _state.update { it.copy(
                        isSubmitting = false,
                        error = result.message
                    )}
                    analytics.dispatch(ConsentEvent.Error(result.message, result.code.name))
                }
            }
        }
    }

    /**
     * Decline consent and close the popup.
     */
    fun declineConsent() {
        analytics.dispatch(ConsentEvent.ConsentDeclined(_state.value.language))
        _state.update { it.copy(
            isPopupVisible = false,
            selectedPurposes = emptySet(),
            age = "",
            validationErrors = emptyMap(),
            error = null
        )}
    }

    // ─── Language ─────────────────────────────────────────────────

    /**
     * Switch the SDK language and refetch purposes.
     *
     * @param langCode Language code (e.g., "en", "fr", "de")
     */
    fun setLanguage(langCode: String) {
        val oldLang = _state.value.language
        if (oldLang == langCode) return

        _state.update { it.copy(language = langCode) }
        analytics.dispatch(ConsentEvent.LanguageChanged(from = oldLang, to = langCode))

        // Refetch purposes in the new language
        if (_state.value.isPopupVisible) {
            fetchPurposes()
        }
    }

    // ─── Status & Reset ───────────────────────────────────────────

    /**
     * Get the current consent submission status.
     *
     * @return true if consent has been submitted in this session
     */
    fun getConsentStatus(): Boolean {
        return _state.value.isConsentSubmitted
    }

    /**
     * Reset all consent state to defaults.
     */
    fun resetConsent() {
        val lang = _state.value.language
        _state.value = ConsentState(language = lang)
    }

    // ─── Cleanup ──────────────────────────────────────────────────

    /**
     * Release all resources. Call when the SDK is no longer needed.
     */
    fun destroy() {
        scope?.cancel()
        api?.close()
        analytics.clearListeners()
        config = null
        api = null
        scope = null
        _state.value = ConsentState()
    }

    // ─── Utilities ────────────────────────────────────────────────

}
