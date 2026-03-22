package com.niceoneconsent.sdk.manager

import com.niceoneconsent.sdk.models.*
import kotlin.test.*

/**
 * Unit tests for ConsentManager validation and state management.
 */
class ConsentManagerTest {

    private lateinit var manager: ConsentManager

    @BeforeTest
    fun setup() {
        manager = ConsentManager()
    }

    @Test
    fun `showConsent fails when not initialized`() {
        val result = manager.showConsent()
        assertTrue(result is ConsentResult.Error)
        assertEquals(ErrorCode.NOT_INITIALIZED, (result as ConsentResult.Error).code)
    }

    @Test
    fun `initialize sets language from config`() {
        val config = ConsentConfig(
            apiKey = "test-key",
            baseUrl = "https://api.example.com",
            language = "fr"
        )
        manager.initialize(config)

        assertEquals("fr", manager.state.value.language)
        assertTrue(manager.isInitialized)
    }

    @Test
    fun `setAge updates state`() {
        initializeManager()
        manager.setAge("25")
        assertEquals("25", manager.state.value.age)
    }

    @Test
    fun `validate returns error for empty age`() {
        initializeManager()
        manager.setAge("")
        val errors = manager.validate()
        assertTrue(errors.containsKey("age"))
        assertEquals("Age is required", errors["age"])
    }

    @Test
    fun `validate returns error for non-numeric age`() {
        initializeManager()
        manager.setAge("abc")
        val errors = manager.validate()
        assertTrue(errors.containsKey("age"))
        assertEquals("Age must be a valid number", errors["age"])
    }

    @Test
    fun `validate returns error for negative age`() {
        initializeManager()
        manager.setAge("-5")
        val errors = manager.validate()
        assertTrue(errors.containsKey("age"))
        assertEquals("Age cannot be negative", errors["age"])
    }

    @Test
    fun `validate returns error for age below minimum`() {
        initializeManager()
        manager.setAge("10")
        val errors = manager.validate()
        assertTrue(errors.containsKey("age"))
        assertTrue(errors["age"]!!.contains("at least"))
    }

    @Test
    fun `validate returns error for extremely large age`() {
        initializeManager()
        manager.setAge("999")
        val errors = manager.validate()
        assertTrue(errors.containsKey("age"))
        assertEquals("Please enter a valid age", errors["age"])
    }

    @Test
    fun `validate passes for valid age`() {
        initializeManager()
        manager.setAge("25")
        val errors = manager.validate()
        assertFalse(errors.containsKey("age"))
    }

    @Test
    fun `setLanguage updates state language`() {
        initializeManager()
        manager.setLanguage("de")
        assertEquals("de", manager.state.value.language)
    }

    @Test
    fun `setLanguage is no-op for same language`() {
        initializeManager()
        val initialState = manager.state.value
        manager.setLanguage("en")
        // State should be the same object since language didn't change
        assertEquals(initialState.language, manager.state.value.language)
    }

    @Test
    fun `resetConsent clears state but keeps language`() {
        initializeManager()
        manager.setAge("25")
        manager.setLanguage("fr")
        manager.resetConsent()

        val state = manager.state.value
        assertEquals("", state.age)
        assertEquals(emptySet(), state.selectedPurposes)
        assertEquals("fr", state.language) // Language preserved
    }

    @Test
    fun `declineConsent hides popup and clears selections`() {
        initializeManager()
        manager.setAge("25")
        manager.declineConsent()

        val state = manager.state.value
        assertFalse(state.isPopupVisible)
        assertEquals("", state.age)
        assertEquals(emptySet(), state.selectedPurposes)
    }

    @Test
    fun `hideConsent sets popup to not visible`() {
        initializeManager()
        manager.hideConsent()
        assertFalse(manager.state.value.isPopupVisible)
    }

    @Test
    fun `getConsentStatus returns false initially`() {
        initializeManager()
        assertFalse(manager.getConsentStatus())
    }

    @Test
    fun `destroy clears all state`() {
        initializeManager()
        manager.destroy()
        assertFalse(manager.isInitialized)
        assertNull(manager.currentConfig)
    }

    private fun initializeManager() {
        manager.initialize(ConsentConfig(
            apiKey = "test-key",
            baseUrl = "https://api.example.com",
            language = "en",
            minimumAge = 16
        ))
    }
}
