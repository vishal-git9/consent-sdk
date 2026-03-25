# react-native-consent-sdk

A robust, highly-customizable React Native wrapper for the Native Consent SDK. This library provides beautiful iOS and Android UI popups for managing user privacy decisions, GDPR consent, and analytics permissions natively.

## Installation

```bash
npm install react-native-consent-sdk
```

### iOS Setup

This package links automatically. Because it uses a native CocoaPod (`NiceOneConsentSDK` targeting **iOS 16.0+**), simply run:

```bash
cd ios
pod install
```

### Android Setup

The native Android SDK is securely hosted on **JitPack**. You **must** add JitPack to your project's repository list so Gradle can resolve the native Android dependencies.

Open your app's rooted `android/build.gradle` (or `android/settings.gradle` depending on your React Native version) and add the JitPack URL:

```gradle
allprojects {
    repositories {
        // ... other repositories
        maven { url 'https://jitpack.io' }
    }
}
```

## Basic Usage

The library provides a globally accessible Singleton `ConsentSDK` which you can use directly. 

### 1. Initialization

You must initialize the SDK before calling any other methods. The best place to do this is at the root of your application (e.g., inside `App.tsx` or `index.js`).

```typescript
import ConsentSDK, { ConsentConfig } from 'react-native-consent-sdk';

const config: ConsentConfig = {
  apiKey: 'YOUR_API_KEY',
  baseUrl: 'https://api.example.com',
  language: 'en', // Defaults to 'en'
  minimumAge: 16,
  theme: {
    primaryColor: '#007BFF',      // Customize the main accent color
    backgroundColor: '#FFFFFF',
    textColor: '#333333',
    buttonRadius: 10,
    // ... other theme options
  }
};

// Initialize SDK natively
await ConsentSDK.initialize(config);
```

### 2. Display the Consent Popup

To cleanly display the cross-platform native privacy popup:

```typescript
try {
  await ConsentSDK.showConsent();
  console.log('Consent process completed seamlessly!');
} catch (error) {
  console.error('Consent failed or was dismissed:', error);
}
```

### 3. Check Consent Status

You can query whether the user has affirmatively submitted their consent:

```typescript
const isConsentGranted = await ConsentSDK.getConsentStatus();
if (isConsentGranted) {
  // Proceed with tracking analytics or targeted ads
}
```

### 4. Language Switching

If your app changes its localized language on the fly, you can instruct the SDK to seamlessly re-fetch all the localized tracking purposes from the backend:

```typescript
await ConsentSDK.setLanguage('fr'); // Switch payload to French
await ConsentSDK.showConsent();     // Popup will render in French!
```

## Advanced Usage (Event Listeners)

If you need deeper granularity over what the user does inside the native popup, you can attach an event listener to capture real-time actions.

```typescript
import { useEffect } from 'react';
import ConsentSDK from 'react-native-consent-sdk';

export default function App() {
  useEffect(() => {
    // Add real-time listener
    const unsubscribe = ConsentSDK.addEventListener((event) => {
      switch (event.type) {
        case 'consentShown':
          console.log('Popup is visible on screen.');
          break;
        case 'consentSubmitted':
          console.log('User legally submitted their preferences!');
          break;
        case 'consentDeclined':
          console.log('User heavily declined optional permissions.');
          break;
        case 'error':
          console.error('An error occurred natively.', event.data);
          break;
      }
    });

    // Cleanup listener on unmount
    return () => unsubscribe();
  }, []);

  return <YourComponents />;
}
```

## License

MIT
