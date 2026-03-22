/// Theme customization for the consent popup UI.
///
/// All color values are specified as hex strings (e.g., "#FF5722").
class ConsentTheme {
  final String primaryColor;
  final String backgroundColor;
  final String textColor;
  final double buttonRadius;
  final String fontFamily;
  final String checkboxColor;
  final String buttonTextColor;
  final String overlayColor;
  final String errorColor;
  final double cardElevation;
  final String secondaryButtonColor;
  final String secondaryButtonTextColor;
  final String inputBorderColor;
  final String inputBackgroundColor;
  final String mandatoryBadgeColor;

  const ConsentTheme({
    this.primaryColor = '#6200EE',
    this.backgroundColor = '#FFFFFF',
    this.textColor = '#212121',
    this.buttonRadius = 8.0,
    this.fontFamily = 'System',
    this.checkboxColor = '#6200EE',
    this.buttonTextColor = '#FFFFFF',
    this.overlayColor = '#80000000',
    this.errorColor = '#B00020',
    this.cardElevation = 8.0,
    this.secondaryButtonColor = '#757575',
    this.secondaryButtonTextColor = '#FFFFFF',
    this.inputBorderColor = '#BDBDBD',
    this.inputBackgroundColor = '#F5F5F5',
    this.mandatoryBadgeColor = '#FF9800',
  });

  /// Default light theme.
  static const light = ConsentTheme();

  /// Default dark theme.
  static const dark = ConsentTheme(
    primaryColor: '#BB86FC',
    backgroundColor: '#121212',
    textColor: '#E0E0E0',
    checkboxColor: '#BB86FC',
    buttonTextColor: '#000000',
    overlayColor: '#CC000000',
    errorColor: '#CF6679',
    secondaryButtonColor: '#424242',
    secondaryButtonTextColor: '#E0E0E0',
    inputBorderColor: '#424242',
    inputBackgroundColor: '#1E1E1E',
    mandatoryBadgeColor: '#FFB74D',
  );

  /// Convert to a map for platform channel serialization.
  Map<String, dynamic> toMap() {
    return {
      'primaryColor': primaryColor,
      'backgroundColor': backgroundColor,
      'textColor': textColor,
      'buttonRadius': buttonRadius,
      'fontFamily': fontFamily,
      'checkboxColor': checkboxColor,
      'buttonTextColor': buttonTextColor,
      'overlayColor': overlayColor,
      'errorColor': errorColor,
      'cardElevation': cardElevation,
      'secondaryButtonColor': secondaryButtonColor,
      'secondaryButtonTextColor': secondaryButtonTextColor,
      'inputBorderColor': inputBorderColor,
      'inputBackgroundColor': inputBackgroundColor,
      'mandatoryBadgeColor': mandatoryBadgeColor,
    };
  }
}
