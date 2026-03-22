# Configuration & Theming

## Initialization

All platforms follow the same pattern: pass a configuration object to `initialize()`.

### Configuration Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `apiKey` | String | — | **Required.** API key for the backend |
| `baseUrl` | String | — | **Required.** API base URL (no trailing slash) |
| `language` | String | `"en"` | Initial language code |
| `theme` | ConsentTheme | Light theme | UI customization |
| `minimumAge` | Int | `16` | Minimum age for consent |
| `timeoutMs` | Long/Int | `15000` | Request timeout (ms) |
| `retryCount` | Int | `2` | Auto-retry count on failure |

---

## Theme Customization

### Theme Properties

| Property | Type | Light Default | Dark Default |
|----------|------|--------------|-------------|
| `primaryColor` | Hex | `#6200EE` | `#BB86FC` |
| `backgroundColor` | Hex | `#FFFFFF` | `#121212` |
| `textColor` | Hex | `#212121` | `#E0E0E0` |
| `buttonRadius` | Float | `8` | `8` |
| `fontFamily` | String | `System` | `System` |
| `checkboxColor` | Hex | `#6200EE` | `#BB86FC` |
| `buttonTextColor` | Hex | `#FFFFFF` | `#000000` |
| `overlayColor` | Hex | `#80000000` | `#CC000000` |
| `errorColor` | Hex | `#B00020` | `#CF6679` |
| `cardElevation` | Float | `8` | `8` |
| `secondaryButtonColor` | Hex | `#757575` | `#424242` |
| `secondaryButtonTextColor` | Hex | `#FFFFFF` | `#E0E0E0` |
| `inputBorderColor` | Hex | `#BDBDBD` | `#424242` |
| `inputBackgroundColor` | Hex | `#F5F5F5` | `#1E1E1E` |
| `mandatoryBadgeColor` | Hex | `#FF9800` | `#FFB74D` |

### Using Built-in Presets

**Android (Kotlin):**
```kotlin
ConsentTheme.Light  // Default light theme
ConsentTheme.Dark   // Default dark theme
```

**iOS (Swift):**
```swift
ConsentTheme.light
ConsentTheme.dark
```

**Flutter (Dart):**
```dart
ConsentTheme.light
ConsentTheme.dark
```

### Custom Theme Example

**Android (Kotlin):**
```kotlin
val customTheme = ConsentTheme(
    primaryColor = "#1976D2",
    backgroundColor = "#FAFAFA",
    textColor = "#333333",
    buttonRadius = 12f,
    checkboxColor = "#1976D2",
    buttonTextColor = "#FFFFFF"
)
```

**iOS (Swift):**
```swift
let customTheme = ConsentTheme(
    primaryColor: Color(hex: "#1976D2"),
    backgroundColor: Color(hex: "#FAFAFA"),
    textColor: Color(hex: "#333333"),
    buttonRadius: 12,
    checkboxColor: Color(hex: "#1976D2")
)
```

**React Native (TypeScript):**
```typescript
const customTheme = {
    primaryColor: '#1976D2',
    backgroundColor: '#FAFAFA',
    textColor: '#333333',
    buttonRadius: 12,
    checkboxColor: '#1976D2',
};
```

**Flutter (Dart):**
```dart
const customTheme = ConsentTheme(
    primaryColor: '#1976D2',
    backgroundColor: '#FAFAFA',
    textColor: '#333333',
    buttonRadius: 12,
    checkboxColor: '#1976D2',
);
```

---

## Analytics Events

Subscribe to consent events for analytics:

| Event | Data | Description |
|-------|------|-------------|
| `consentShown` | language | Popup displayed |
| `consentSubmitted` | selectedPurposes, age, language | Consent submitted |
| `consentDeclined` | language | User declined |
| `consentDismissed` | — | Popup dismissed |
| `languageChanged` | from, to | Language switched |
| `error` | message, code | Error occurred |

**Android:**
```kotlin
ConsentSDK.addEventListener { event ->
    when (event) {
        is ConsentEvent.ConsentSubmitted -> {
            analytics.track("consent_submitted", event.selectedPurposes)
        }
        is ConsentEvent.Error -> {
            crashlytics.log("Consent error: ${event.message}")
        }
        else -> {}
    }
}
```

**iOS:**
```swift
class MyAnalytics: ConsentEventListener {
    func onEvent(_ event: ConsentEvent) {
        switch event {
        case .consentSubmitted(let purposes, let age, let lang):
            Analytics.track("consent_submitted", purposes: purposes)
        case .error(let message, let code):
            Crashlytics.log("Consent error: \(message)")
        default: break
        }
    }
}
```
