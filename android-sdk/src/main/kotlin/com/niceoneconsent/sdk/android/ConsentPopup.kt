package com.niceoneconsent.sdk.android

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.niceoneconsent.sdk.models.ConsentState
import com.niceoneconsent.sdk.models.ConsentTheme
import com.niceoneconsent.sdk.models.Purpose

/**
 * Main consent popup composable.
 *
 * Displays a full-screen dimmed overlay with a centered card containing:
 * - Title
 * - Age input field
 * - Scrollable purpose checklist
 * - Update Consent / Decline buttons
 */
internal @Composable
fun ConsentPopup(
    state: ConsentState,
    theme: ConsentTheme,
    onAgeChanged: (String) -> Unit,
    onPurposeToggled: (String) -> Unit,
    onSubmit: () -> Unit,
    onDecline: () -> Unit,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = ConsentThemeMapper
    val focusManager = LocalFocusManager.current
    val buttonShape = RoundedCornerShape(theme.buttonRadius.dp)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.overlayColor(theme))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                ),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .fillMaxHeight(0.85f)
                    .shadow(
                        elevation = theme.cardElevation.dp,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { /* Consume click to prevent dismissal */ }
                    ),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colors.backgroundColor(theme)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // ─── Header ───────────────────────────────
                    ConsentHeader(theme)

                    Spacer(modifier = Modifier.height(20.dp))

                    // ─── Content Area ─────────────────────────
                    when {
                        state.isLoading -> {
                            LoadingContent(theme = theme, modifier = Modifier.weight(1f))
                        }
                        state.error != null && state.purposes.isEmpty() -> {
                            ErrorContent(
                                error = state.error,
                                theme = theme,
                                onRetry = onRetry,
                                buttonShape = buttonShape,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        state.purposes.isEmpty() && !state.isLoading -> {
                            EmptyContent(theme = theme, onDismiss = onDismiss, buttonShape = buttonShape, modifier = Modifier.weight(1f))
                        }
                        else -> {
                            // Age Input
                            AgeInput(
                                age = state.age,
                                error = state.validationErrors["age"],
                                theme = theme,
                                onAgeChanged = onAgeChanged
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Error Banner (for submission errors)
                            if (state.error != null) {
                                ErrorBanner(error = state.error, theme = theme)
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            // Purpose List
                            Text(
                                text = "Select Purposes",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.textColor(theme),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                            )

                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                            ) {
                                items(state.purposes) { purpose ->
                                    PurposeItem(
                                        purpose = purpose,
                                        isSelected = purpose.id in state.selectedPurposes,
                                        theme = theme,
                                        onToggle = { onPurposeToggled(purpose.id) }
                                    )
                                }
                            }

                            // Validation error for purposes
                            state.validationErrors["purposes"]?.let { error ->
                                Text(
                                    text = error,
                                    color = colors.errorColor(theme),
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Buttons
                            ActionButtons(
                                isSubmitting = state.isSubmitting,
                                theme = theme,
                                buttonShape = buttonShape,
                                onSubmit = {
                                    focusManager.clearFocus()
                                    onSubmit()
                                },
                                onDecline = onDecline
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── Sub-composables ──────────────────────────────────────────────

@Composable
private fun ConsentHeader(theme: ConsentTheme) {
    val colors = ConsentThemeMapper

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Decorative icon
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            colors.primaryColor(theme),
                            colors.primaryColor(theme).copy(alpha = 0.7f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "✓",
                color = colors.buttonTextColor(theme),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Consent Preferences",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = colors.textColor(theme),
            textAlign = TextAlign.Center
        )

        Text(
            text = "Please review and update your consent preferences below.",
            fontSize = 13.sp,
            color = colors.textColor(theme).copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, start = 16.dp, end = 16.dp)
        )
    }
}

@Composable
private fun AgeInput(
    age: String,
    error: String?,
    theme: ConsentTheme,
    onAgeChanged: (String) -> Unit
) {
    val colors = ConsentThemeMapper

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Your Age",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.textColor(theme),
            modifier = Modifier.padding(bottom = 6.dp)
        )

        OutlinedTextField(
            value = age,
            onValueChange = { value ->
                // Only allow numeric input
                if (value.isEmpty() || value.all { it.isDigit() }) {
                    onAgeChanged(value)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    "Enter your age",
                    color = colors.textColor(theme).copy(alpha = 0.4f)
                )
            },
            singleLine = true,
            isError = error != null,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { /* Dismiss keyboard */ }
            ),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colors.primaryColor(theme),
                unfocusedBorderColor = colors.inputBorderColor(theme),
                errorBorderColor = colors.errorColor(theme),
                focusedContainerColor = colors.inputBackgroundColor(theme),
                unfocusedContainerColor = colors.inputBackgroundColor(theme),
                focusedTextColor = colors.textColor(theme),
                unfocusedTextColor = colors.textColor(theme),
                cursorColor = colors.primaryColor(theme)
            )
        )

        if (error != null) {
            Text(
                text = error,
                color = colors.errorColor(theme),
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
    }
}

@Composable
private fun PurposeItem(
    purpose: Purpose,
    isSelected: Boolean,
    theme: ConsentTheme,
    onToggle: () -> Unit
) {
    val colors = ConsentThemeMapper

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(enabled = !purpose.mandatory) { onToggle() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                colors.primaryColor(theme).copy(alpha = 0.08f)
            else
                colors.inputBackgroundColor(theme)
        ),
        border = if (isSelected) BorderStroke(
            1.dp,
            colors.primaryColor(theme).copy(alpha = 0.3f)
        ) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { if (!purpose.mandatory) onToggle() },
                enabled = !purpose.mandatory,
                colors = CheckboxDefaults.colors(
                    checkedColor = colors.checkboxColor(theme),
                    uncheckedColor = colors.inputBorderColor(theme),
                    checkmarkColor = colors.buttonTextColor(theme),
                    disabledCheckedColor = colors.checkboxColor(theme).copy(alpha = 0.7f)
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = purpose.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.textColor(theme),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (purpose.mandatory) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Required",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.mandatoryBadgeColor(theme),
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    colors.mandatoryBadgeColor(theme).copy(alpha = 0.15f)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = purpose.description,
                    fontSize = 12.sp,
                    color = colors.textColor(theme).copy(alpha = 0.6f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun ActionButtons(
    isSubmitting: Boolean,
    theme: ConsentTheme,
    buttonShape: RoundedCornerShape,
    onSubmit: () -> Unit,
    onDecline: () -> Unit
) {
    val colors = ConsentThemeMapper

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Primary button: Update Consent
        Button(
            onClick = onSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !isSubmitting,
            shape = buttonShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.primaryColor(theme),
                contentColor = colors.buttonTextColor(theme),
                disabledContainerColor = colors.primaryColor(theme).copy(alpha = 0.5f)
            )
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = colors.buttonTextColor(theme),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Submitting...", fontWeight = FontWeight.SemiBold)
            } else {
                Text("Update Consent", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Secondary button: Decline
        OutlinedButton(
            onClick = onDecline,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !isSubmitting,
            shape = buttonShape,
            border = BorderStroke(1.dp, colors.secondaryButtonColor(theme)),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = colors.secondaryButtonColor(theme)
            )
        ) {
            Text("Decline", fontWeight = FontWeight.Medium, fontSize = 15.sp)
        }
    }
}

@Composable
private fun LoadingContent(theme: ConsentTheme, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                color = ConsentThemeMapper.primaryColor(theme),
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Loading consent options...",
                color = ConsentThemeMapper.textColor(theme).copy(alpha = 0.6f),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun ErrorContent(
    error: String?,
    theme: ConsentTheme,
    onRetry: () -> Unit,
    buttonShape: RoundedCornerShape,
    modifier: Modifier = Modifier
) {
    val colors = ConsentThemeMapper

    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "⚠",
                fontSize = 40.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = error ?: "Something went wrong",
                color = colors.errorColor(theme),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRetry,
                shape = buttonShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primaryColor(theme)
                )
            ) {
                Text("Retry", color = colors.buttonTextColor(theme))
            }
        }
    }
}

@Composable
private fun EmptyContent(
    theme: ConsentTheme,
    onDismiss: () -> Unit,
    buttonShape: RoundedCornerShape,
    modifier: Modifier = Modifier
) {
    val colors = ConsentThemeMapper

    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "📋",
                fontSize = 40.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No consent options available",
                color = colors.textColor(theme),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onDismiss,
                shape = buttonShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primaryColor(theme)
                )
            ) {
                Text("Close", color = colors.buttonTextColor(theme))
            }
        }
    }
}

@Composable
private fun ErrorBanner(error: String?, theme: ConsentTheme) {
    val colors = ConsentThemeMapper

    if (error != null) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = colors.errorColor(theme).copy(alpha = 0.1f)
            )
        ) {
            Text(
                text = error,
                color = colors.errorColor(theme),
                fontSize = 12.sp,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}
