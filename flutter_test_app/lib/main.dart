import 'package:flutter/material.dart';
import 'package:consent_sdk/consent_sdk.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Consent SDK Flutter Demo',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.blue),
        useMaterial3: true,
      ),
      home: const MyHomePage(title: 'Consent SDK Flutter Demo'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});
  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  String _status = 'Unchecked';

  @override
  void initState() {
    super.initState();
    _initSdk();
  }

  Future<void> _initSdk() async {
    try {
      await ConsentSDK.initialize(
        const ConsentConfig(
          apiKey: 'test-api-key',
          // 10.0.2.2 is localhost for Android Emulators
          // localhost for iOS Simulators
          baseUrl: 'http://10.0.2.2:3000',
          language: 'en',
          theme: ConsentTheme.light,
          minimumAge: 16,
        ),
      );
      
      // Since EventChannel is not implemented in the flutter plugin yet,
      // we'll just check status on startup.
      await _checkStatus();
    } catch (e) {
      print('Failed to initialize SDK: $e');
    }
  }

  Future<void> _checkStatus() async {
    try {
      final isConsented = await ConsentSDK.getConsentStatus();
      setState(() {
        _status = isConsented ? 'Consented (True)' : 'Not Consented (False)';
      });
    } catch (e) {
      print('Failed to get status: $e');
    }
  }

  Future<void> _handleShowConsent() async {
    try {
      await ConsentSDK.showConsent();
      // Wait a moment then check status since we don't have events
      await Future.delayed(const Duration(seconds: 1));
      await _checkStatus();
    } catch (e) {
      _showAlert('Error', 'Failed to show consent: $e');
    }
  }

  Future<void> _handleReset() async {
    try {
      await ConsentSDK.resetConsent();
      await _checkStatus();
      _showAlert('Reset', 'Consent status has been reset');
    } catch (e) {
      _showAlert('Error', 'Failed to reset consent: $e');
    }
  }

  void _showAlert(String title, String message) {
    if (!mounted) return;
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text(title),
        content: Text(message),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('OK'),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF5F7FA),
      appBar: AppBar(
        title: Text(widget.title),
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
      ),
      body: Padding(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            ElevatedButton(
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFF0066CC),
                foregroundColor: Colors.white,
                padding: const EdgeInsets.symmetric(vertical: 16),
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(12),
                ),
              ),
              onPressed: _handleShowConsent,
              child: const Text('Show Consent Popup', style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
            ),
            const SizedBox(height: 16),
            OutlinedButton(
              style: OutlinedButton.styleFrom(
                foregroundColor: const Color(0xFF0066CC),
                side: const BorderSide(color: Color(0xFF0066CC)),
                padding: const EdgeInsets.symmetric(vertical: 16),
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(12),
                ),
              ),
              onPressed: _checkStatus,
              child: const Text('Check Status', style: TextStyle(fontSize: 16, fontWeight: FontWeight.w600)),
            ),
            const SizedBox(height: 16),
            OutlinedButton(
              style: OutlinedButton.styleFrom(
                foregroundColor: const Color(0xFF0066CC),
                side: const BorderSide(color: Color(0xFF0066CC)),
                padding: const EdgeInsets.symmetric(vertical: 16),
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(12),
                ),
              ),
              onPressed: _handleReset,
              child: const Text('Reset Consent', style: TextStyle(fontSize: 16, fontWeight: FontWeight.w600)),
            ),
            const SizedBox(height: 32),
            Container(
              padding: const EdgeInsets.all(20),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(12),
                boxShadow: [
                  BoxShadow(
                    color: Colors.black.withOpacity(0.05),
                    offset: const Offset(0, 2),
                    blurRadius: 4,
                  ),
                ],
              ),
              child: Column(
                children: [
                  const Text(
                    'Current Status:',
                    style: TextStyle(fontSize: 14, color: Colors.black54),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    _status,
                    style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
