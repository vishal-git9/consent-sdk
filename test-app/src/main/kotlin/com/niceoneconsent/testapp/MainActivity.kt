package com.niceoneconsent.testapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.niceoneconsent.sdk.analytics.ConsentEvent
import com.niceoneconsent.sdk.android.ConsentSDK
import com.niceoneconsent.sdk.models.ConsentConfig
import com.niceoneconsent.sdk.models.ConsentTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Use 10.0.2.2 for Android Emulator to hit localhost on the host machine
        val localhostForEmulator = "http://10.0.2.2:3000"

        // Initialize SDK
        ConsentSDK.initialize(
            ConsentConfig(
                apiKey = "test-api-key",
                baseUrl = localhostForEmulator,
                language = "en",
                theme = ConsentTheme.Light,
                minimumAge = 16
            )
        )

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TestAppScreen(
                        onShowConsent = {
                            ConsentSDK.showConsent(this@MainActivity)
                        },
                        onResetConsent = {
                            ConsentSDK.resetConsent()
                            Toast.makeText(this@MainActivity, "Consent reset", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }

        // Add analytics listener for feedback
        ConsentSDK.addEventListener { event ->
            when (event) {
                is ConsentEvent.ConsentSubmitted -> {
                    Toast.makeText(this, "Consent Submitted! Purposes: ${event.selectedPurposes.size}", Toast.LENGTH_SHORT).show()
                }
                is ConsentEvent.ConsentDeclined -> {
                    Toast.makeText(this, "Consent Declined", Toast.LENGTH_SHORT).show()
                }
                is ConsentEvent.Error -> {
                    Toast.makeText(this, "Error: ${event.message}", Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ConsentSDK.destroy()
    }
}

@Composable
fun TestAppScreen(
    onShowConsent: () -> Unit,
    onResetConsent: () -> Unit
) {
    var status by remember { mutableStateOf("Status: Checked on load") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "SDK Test Application",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onShowConsent,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Show Consent Popup")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = {
                val isConsented = ConsentSDK.getConsentStatus()
                status = "Consented: $isConsented"
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Check Status")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onResetConsent,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reset Consent")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = status,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
