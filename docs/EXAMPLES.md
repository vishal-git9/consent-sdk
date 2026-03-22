# Examples

Complete integration examples for each platform.

---

## Android (Kotlin)

```kotlin
import com.niceoneconsent.sdk.android.ConsentSDK
import com.niceoneconsent.sdk.models.ConsentConfig
import com.niceoneconsent.sdk.models.ConsentTheme
import com.niceoneconsent.sdk.analytics.ConsentEvent

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Initialize the SDK
        ConsentSDK.initialize(ConsentConfig(
            apiKey = "your-api-key-here",
            baseUrl = "https://api.example.com",
            language = "en",
            theme = ConsentTheme(
                primaryColor = "#1976D2",
                backgroundColor = "#FFFFFF",
                buttonRadius = 12f
            ),
            minimumAge = 16
        ))

        // 2. Add analytics listener (optional)
        ConsentSDK.addEventListener { event ->
            when (event) {
                is ConsentEvent.ConsentSubmitted -> {
                    Log.d("Consent", "Submitted: ${event.selectedPurposes}")
                }
                is ConsentEvent.ConsentDeclined -> {
                    Log.d("Consent", "Declined")
                }
                is ConsentEvent.Error -> {
                    Log.e("Consent", "Error: ${event.message}")
                }
                else -> {}
            }
        }

        // 3. Show consent popup
        findViewById<Button>(R.id.showConsentBtn).setOnClickListener {
            ConsentSDK.showConsent(this)
        }

        // 4. Switch language
        findViewById<Button>(R.id.switchLangBtn).setOnClickListener {
            ConsentSDK.setLanguage("fr")
        }

        // 5. Check consent status
        val isConsented = ConsentSDK.getConsentStatus()

        // 6. Reset consent
        // ConsentSDK.resetConsent()
    }

    override fun onDestroy() {
        super.onDestroy()
        ConsentSDK.destroy()
    }
}
```

---

## iOS (Swift)

```swift
import ConsentSDK

class ViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()

        // 1. Initialize the SDK
        ConsentSDK.shared.initialize(config: ConsentConfig(
            apiKey: "your-api-key-here",
            baseUrl: "https://api.example.com",
            language: "en",
            theme: ConsentTheme(
                primaryColor: Color(hex: "#1976D2"),
                backgroundColor: .white,
                buttonRadius: 12
            ),
            minimumAge: 16
        ))

        // 2. Add analytics listener (optional)
        ConsentSDK.shared.addEventListener(self)
    }

    @IBAction func showConsentTapped(_ sender: Any) {
        // 3. Show consent popup
        ConsentSDK.shared.showConsent(from: self)
    }

    @IBAction func switchLanguageTapped(_ sender: Any) {
        // 4. Switch language
        ConsentSDK.shared.setLanguage("fr")
    }

    deinit {
        ConsentSDK.shared.destroy()
    }
}

// Analytics listener
extension ViewController: ConsentEventListener {
    func onEvent(_ event: ConsentEvent) {
        switch event {
        case .consentSubmitted(let purposes, let age, let lang):
            print("Consent submitted: \(purposes)")
        case .consentDeclined(let lang):
            print("Consent declined")
        case .error(let message, let code):
            print("Error: \(message)")
        default:
            break
        }
    }
}
```

---

## React Native (TypeScript)

```typescript
import ConsentSDK from 'react-native-consent-sdk';

// 1. Initialize (e.g., in App.tsx useEffect)
await ConsentSDK.initialize({
  apiKey: 'your-api-key-here',
  baseUrl: 'https://api.example.com',
  language: 'en',
  theme: {
    primaryColor: '#1976D2',
    backgroundColor: '#FFFFFF',
    buttonRadius: 12,
  },
  minimumAge: 16,
});

// 2. Add analytics listener (optional)
const unsubscribe = ConsentSDK.addEventListener((event) => {
  console.log('Consent event:', event.type, event.data);
});

// 3. Show consent popup
const handleShowConsent = async () => {
  try {
    await ConsentSDK.showConsent();
  } catch (error) {
    console.error('Failed to show consent:', error);
  }
};

// 4. Switch language
await ConsentSDK.setLanguage('fr');

// 5. Check consent status
const isConsented = await ConsentSDK.getConsentStatus();

// 6. Reset consent
await ConsentSDK.resetConsent();

// Cleanup
unsubscribe();
```

---

## Flutter (Dart)

```dart
import 'package:consent_sdk/consent_sdk.dart';

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {

  @override
  void initState() {
    super.initState();
    _initializeSDK();
  }

  Future<void> _initializeSDK() async {
    // 1. Initialize the SDK
    await ConsentSDK.initialize(const ConsentConfig(
      apiKey: 'your-api-key-here',
      baseUrl: 'https://api.example.com',
      language: 'en',
      theme: ConsentTheme(
        primaryColor: '#1976D2',
        backgroundColor: '#FFFFFF',
        buttonRadius: 12,
      ),
      minimumAge: 16,
    ));
  }

  // 2. Show consent popup
  Future<void> _showConsent() async {
    try {
      await ConsentSDK.showConsent();
    } catch (e) {
      print('Failed to show consent: $e');
    }
  }

  // 3. Switch language
  Future<void> _switchLanguage() async {
    await ConsentSDK.setLanguage('fr');
  }

  // 4. Check consent status
  Future<void> _checkStatus() async {
    final isConsented = await ConsentSDK.getConsentStatus();
    print('Consent status: $isConsented');
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(title: const Text('Consent Demo')),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              ElevatedButton(
                onPressed: _showConsent,
                child: const Text('Show Consent'),
              ),
              const SizedBox(height: 16),
              ElevatedButton(
                onPressed: _switchLanguage,
                child: const Text('Switch to French'),
              ),
              const SizedBox(height: 16),
              ElevatedButton(
                onPressed: _checkStatus,
                child: const Text('Check Status'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
```
