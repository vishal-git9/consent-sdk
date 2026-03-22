package com.niceoneconsent.sdk.models

/**
 * Theme customization for the consent popup UI.
 *
 * All color values are specified as hex strings (e.g., "#FF5722").
 * Clients may pass this during SDK initialization to override defaults.
 */
data class ConsentTheme(
    val primaryColor: String = "#6200EE",
    val backgroundColor: String = "#FFFFFF",
    val textColor: String = "#212121",
    val buttonRadius: Float = 8f,
    val fontFamily: String = "System",
    val checkboxColor: String = "#6200EE",
    val buttonTextColor: String = "#FFFFFF",
    val overlayColor: String = "#80000000",
    val errorColor: String = "#B00020",
    val cardElevation: Float = 8f,
    val secondaryButtonColor: String = "#757575",
    val secondaryButtonTextColor: String = "#FFFFFF",
    val inputBorderColor: String = "#BDBDBD",
    val inputBackgroundColor: String = "#F5F5F5",
    val mandatoryBadgeColor: String = "#FF9800"
) {
    companion object {
        /** Default light theme. */
        val Light = ConsentTheme()

        /** Default dark theme. */
        val Dark = ConsentTheme(
            primaryColor = "#BB86FC",
            backgroundColor = "#121212",
            textColor = "#E0E0E0",
            checkboxColor = "#BB86FC",
            buttonTextColor = "#000000",
            overlayColor = "#CC000000",
            errorColor = "#CF6679",
            secondaryButtonColor = "#424242",
            secondaryButtonTextColor = "#E0E0E0",
            inputBorderColor = "#424242",
            inputBackgroundColor = "#1E1E1E",
            mandatoryBadgeColor = "#FFB74D"
        )
    }
}
