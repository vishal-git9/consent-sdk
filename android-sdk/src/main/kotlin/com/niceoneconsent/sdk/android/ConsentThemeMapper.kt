package com.niceoneconsent.sdk.android

import androidx.compose.ui.graphics.Color
import com.niceoneconsent.sdk.models.ConsentTheme

/**
 * Maps [ConsentTheme] hex color strings to Compose [Color] values.
 */
internal object ConsentThemeMapper {

    fun primaryColor(theme: ConsentTheme) = parseColor(theme.primaryColor)
    fun backgroundColor(theme: ConsentTheme) = parseColor(theme.backgroundColor)
    fun textColor(theme: ConsentTheme) = parseColor(theme.textColor)
    fun checkboxColor(theme: ConsentTheme) = parseColor(theme.checkboxColor)
    fun buttonTextColor(theme: ConsentTheme) = parseColor(theme.buttonTextColor)
    fun overlayColor(theme: ConsentTheme) = parseColor(theme.overlayColor)
    fun errorColor(theme: ConsentTheme) = parseColor(theme.errorColor)
    fun secondaryButtonColor(theme: ConsentTheme) = parseColor(theme.secondaryButtonColor)
    fun secondaryButtonTextColor(theme: ConsentTheme) = parseColor(theme.secondaryButtonTextColor)
    fun inputBorderColor(theme: ConsentTheme) = parseColor(theme.inputBorderColor)
    fun inputBackgroundColor(theme: ConsentTheme) = parseColor(theme.inputBackgroundColor)
    fun mandatoryBadgeColor(theme: ConsentTheme) = parseColor(theme.mandatoryBadgeColor)

    /**
     * Parse a hex color string (e.g., "#FF5722" or "#80FF5722") to Compose [Color].
     */
    private fun parseColor(hex: String): Color {
        return try {
            Color(android.graphics.Color.parseColor(hex))
        } catch (e: Exception) {
            Color.Gray
        }
    }
}
