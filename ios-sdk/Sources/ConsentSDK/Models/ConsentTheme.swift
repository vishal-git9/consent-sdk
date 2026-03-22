import SwiftUI

/// Theme customization for the consent popup UI.
///
/// Clients pass this during SDK initialization to override the default styling.
public struct ConsentTheme {
    public var primaryColor: Color
    public var backgroundColor: Color
    public var textColor: Color
    public var buttonRadius: CGFloat
    public var fontFamily: String
    public var checkboxColor: Color
    public var buttonTextColor: Color
    public var overlayColor: Color
    public var errorColor: Color
    public var cardElevation: CGFloat
    public var secondaryButtonColor: Color
    public var secondaryButtonTextColor: Color
    public var inputBorderColor: Color
    public var inputBackgroundColor: Color
    public var mandatoryBadgeColor: Color

    public init(
        primaryColor: Color = Color(hex: "#6200EE"),
        backgroundColor: Color = .white,
        textColor: Color = Color(hex: "#212121"),
        buttonRadius: CGFloat = 8,
        fontFamily: String = "System",
        checkboxColor: Color = Color(hex: "#6200EE"),
        buttonTextColor: Color = .white,
        overlayColor: Color = Color.black.opacity(0.5),
        errorColor: Color = Color(hex: "#B00020"),
        cardElevation: CGFloat = 8,
        secondaryButtonColor: Color = Color(hex: "#757575"),
        secondaryButtonTextColor: Color = .white,
        inputBorderColor: Color = Color(hex: "#BDBDBD"),
        inputBackgroundColor: Color = Color(hex: "#F5F5F5"),
        mandatoryBadgeColor: Color = Color(hex: "#FF9800")
    ) {
        self.primaryColor = primaryColor
        self.backgroundColor = backgroundColor
        self.textColor = textColor
        self.buttonRadius = buttonRadius
        self.fontFamily = fontFamily
        self.checkboxColor = checkboxColor
        self.buttonTextColor = buttonTextColor
        self.overlayColor = overlayColor
        self.errorColor = errorColor
        self.cardElevation = cardElevation
        self.secondaryButtonColor = secondaryButtonColor
        self.secondaryButtonTextColor = secondaryButtonTextColor
        self.inputBorderColor = inputBorderColor
        self.inputBackgroundColor = inputBackgroundColor
        self.mandatoryBadgeColor = mandatoryBadgeColor
    }

    /// Default light theme.
    public static let light = ConsentTheme()

    /// Default dark theme.
    public static let dark = ConsentTheme(
        primaryColor: Color(hex: "#BB86FC"),
        backgroundColor: Color(hex: "#121212"),
        textColor: Color(hex: "#E0E0E0"),
        checkboxColor: Color(hex: "#BB86FC"),
        buttonTextColor: .black,
        overlayColor: Color.black.opacity(0.7),
        errorColor: Color(hex: "#CF6679"),
        secondaryButtonColor: Color(hex: "#424242"),
        secondaryButtonTextColor: Color(hex: "#E0E0E0"),
        inputBorderColor: Color(hex: "#424242"),
        inputBackgroundColor: Color(hex: "#1E1E1E"),
        mandatoryBadgeColor: Color(hex: "#FFB74D")
    )
}

// MARK: - Color Hex Extension

public extension Color {
    init(hex: String) {
        let hex = hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        var int: UInt64 = 0
        Scanner(string: hex).scanHexInt64(&int)

        let a, r, g, b: UInt64
        switch hex.count {
        case 6: // RGB
            (a, r, g, b) = (255, (int >> 16) & 0xFF, (int >> 8) & 0xFF, int & 0xFF)
        case 8: // ARGB
            (a, r, g, b) = ((int >> 24) & 0xFF, (int >> 16) & 0xFF, (int >> 8) & 0xFF, int & 0xFF)
        default:
            (a, r, g, b) = (255, 128, 128, 128)
        }

        self.init(
            .sRGB,
            red: Double(r) / 255,
            green: Double(g) / 255,
            blue: Double(b) / 255,
            opacity: Double(a) / 255
        )
    }
}
