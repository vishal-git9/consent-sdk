# Consent SDK

A **production-grade, cross-platform Consent Popup SDK** for managing user consent in mobile applications. Provides a beautiful, customizable popup UI with purpose management, language switching, and comprehensive theming.

## Platforms

| Platform | Technology | Status |
|----------|-----------|--------|
| **Core** | Kotlin Multiplatform | ✅ |
| **Android** | Jetpack Compose | ✅ |
| **iOS** | SwiftUI | ✅ |
| **React Native** | Native Modules | ✅ |
| **Flutter** | Platform Channels | ✅ |

## Features

- 🎨 **Customizable UI** — Full theme support with light/dark mode presets
- 🌍 **Multi-language** — Dynamic language switching with auto-refetch
- ✅ **Validation** — Age validation, mandatory purpose enforcement
- 🔄 **Retry Logic** — Automatic retries with exponential backoff
- 📊 **Analytics Hooks** — Event listeners for consent lifecycle tracking
- 🔒 **Security** — HTTPS only, no API key logging, secure data handling
- 🛡️ **Edge Cases** — Handles offline, empty responses, double submissions, duplicate popups

## Quick Start

See platform-specific guides:

- [Installation](docs/INSTALLATION.md)
- [Configuration & Theming](docs/CONFIGURATION.md)
- [API Reference](docs/API_REFERENCE.md)
- [Examples](docs/EXAMPLES.md)

## Project Structure

```
consent-sdk/
├── core/                       # Kotlin Multiplatform shared logic
├── android-sdk/                # Android library (Jetpack Compose)
├── ios-sdk/                    # iOS framework (SwiftUI)
├── react-native-wrapper/       # React Native native module
├── flutter-plugin/             # Flutter platform channel plugin
├── mock-server/                # Node.js mock API server
└── docs/                       # Documentation
```

## Mock Server

For local development and testing:

```bash
cd mock-server
npm install
npm start
# Server runs at http://localhost:3000
```

## License

MIT
