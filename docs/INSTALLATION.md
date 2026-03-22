# Installation

## Android

### Gradle (Local module)

Add the SDK modules to your project's `settings.gradle.kts`:

```kotlin
include(":consent-sdk-core")
project(":consent-sdk-core").projectDir = file("path/to/consent-sdk/core")

include(":consent-sdk-android")
project(":consent-sdk-android").projectDir = file("path/to/consent-sdk/android-sdk")
```

Add the dependency in your app's `build.gradle.kts`:

```kotlin
dependencies {
    implementation(project(":consent-sdk-android"))
}
```

### AAR (Published artifact)

```kotlin
dependencies {
    implementation("com.niceoneconsent:consent-sdk-android:1.0.0")
}
```

### AndroidManifest.xml

Register the consent activity in your app's manifest:

```xml
<activity
    android:name="com.niceoneconsent.sdk.android.ConsentActivity"
    android:theme="@android:style/Theme.Translucent.NoTitleBar"
    android:configChanges="orientation|screenSize|keyboardHidden"
    android:windowSoftInputMode="adjustResize" />
```

---

## iOS

### Swift Package Manager

Add the package in Xcode:

1. File → Add Package Dependencies
2. Enter: `https://github.com/niceoneconsent/consent-sdk-ios`
3. Select version `1.0.0`

Or add to your `Package.swift`:

```swift
dependencies: [
    .package(url: "https://github.com/niceoneconsent/consent-sdk-ios", from: "1.0.0")
]
```

### CocoaPods

```ruby
pod 'ConsentSDK', '~> 1.0.0'
```

---

## React Native

### npm

```bash
npm install react-native-consent-sdk
```

### yarn

```bash
yarn add react-native-consent-sdk
```

### iOS Pod Install

```bash
cd ios && pod install
```

### Android Auto-linking

React Native 0.60+ supports auto-linking. No additional setup required.

For manual linking, add to `MainApplication.kt`:

```kotlin
import com.niceoneconsent.sdk.reactnative.ConsentSDKPackage

// In getPackages():
packages.add(ConsentSDKPackage())
```

---

## Flutter

### pubspec.yaml

```yaml
dependencies:
  consent_sdk:
    path: path/to/consent-sdk/flutter-plugin
```

Or from pub.dev:

```yaml
dependencies:
  consent_sdk: ^1.0.0
```

---

## Mock Server (for development)

```bash
cd mock-server
npm install
npm start
```

The server starts at `http://localhost:3000` with endpoints:
- `GET /consent/purposes?lang=en`
- `POST /consent`
- `GET /health`
