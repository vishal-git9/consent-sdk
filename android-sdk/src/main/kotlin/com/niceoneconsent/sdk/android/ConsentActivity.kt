package com.niceoneconsent.sdk.android

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

/**
 * Transparent activity that hosts the consent popup as a Compose overlay.
 *
 * This activity has a translucent theme so it appears over the calling activity
 * with a dimmed background.
 */
class ConsentActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Make status and nav bars transparent for overlay effect
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )

        val manager = ConsentSDK.manager

        setContent {
            val state by manager.state.collectAsState()

            // Close activity when popup is hidden
            LaunchedEffect(state.isPopupVisible) {
                if (!state.isPopupVisible && !state.isConsentSubmitted) {
                    // Only close if popup was explicitly hidden, not on initial load
                }
            }

            ConsentPopup(
                state = state,
                theme = manager.currentConfig?.theme ?: com.niceoneconsent.sdk.models.ConsentTheme.Light,
                onAgeChanged = { manager.setAge(it) },
                onPurposeToggled = { manager.togglePurpose(it) },
                onSubmit = { manager.submitConsent() },
                onDecline = {
                    manager.declineConsent()
                    finish()
                    overridePendingTransition(0, android.R.anim.fade_out)
                },
                onRetry = { manager.fetchPurposes() },
                onDismiss = {
                    manager.hideConsent()
                    finish()
                    overridePendingTransition(0, android.R.anim.fade_out)
                }
            )

            // Auto-close on successful submission
            LaunchedEffect(state.isConsentSubmitted) {
                if (state.isConsentSubmitted) {
                    kotlinx.coroutines.delay(500) // Brief delay to show success
                    finish()
                    overridePendingTransition(0, android.R.anim.fade_out)
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        ConsentSDK.manager.declineConsent()
        super.onBackPressed()
        overridePendingTransition(0, android.R.anim.fade_out)
    }
}
