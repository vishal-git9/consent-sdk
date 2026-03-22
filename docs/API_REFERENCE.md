# API Reference

## Public Methods

All platforms expose the same set of core methods:

---

### `initialize(config)`

Initialize the SDK. **Must be called before any other methods.**

| Param | Type | Description |
|-------|------|-------------|
| `config` | ConsentConfig | SDK configuration |

**Returns:** void

---

### `showConsent()`

Display the consent popup. Automatically fetches purposes.

**Returns:** Result (success/error)

**Error codes:**
- `NOT_INITIALIZED` — SDK not initialized
- `ALREADY_VISIBLE` — Popup is already showing

---

### `hideConsent()`

Programmatically hide the consent popup.

**Returns:** void

---

### `setLanguage(langCode)`

Switch the SDK language. If the popup is visible, purposes are refetched automatically.

| Param | Type | Description |
|-------|------|-------------|
| `langCode` | String | Language code (e.g., "en", "fr", "de") |

**Returns:** void

---

### `getConsentStatus()`

Check if consent has been submitted in the current session.

**Returns:** Boolean

---

### `resetConsent()`

Reset all consent state (age, selected purposes) to defaults. Language is preserved.

**Returns:** void

---

### `addEventListener(listener)` / `removeEventListener(listener)`

Subscribe to / unsubscribe from consent analytics events.

| Event | Description |
|-------|-------------|
| `consentShown` | Popup was displayed |
| `consentSubmitted` | Consent was submitted |
| `consentDeclined` | User declined consent |
| `consentDismissed` | Popup was dismissed |
| `languageChanged` | Language was switched |
| `error` | An error occurred |

---

### `destroy()`

Release all SDK resources. Call when the SDK is no longer needed.

**Returns:** void

---

## Backend API

### `GET /consent/purposes`

Fetch consent purposes for a language.

| Query Param | Type | Default | Description |
|-------------|------|---------|-------------|
| `lang` | String | `"en"` | Language code |

**Response:**
```json
{
  "purposes": [
    {
      "id": "sales",
      "name": "Sales",
      "description": "Allow contact from sales team",
      "mandatory": false
    }
  ]
}
```

---

### `POST /consent`

Submit user consent.

**Request Body:**
```json
{
  "age": 25,
  "selectedPurposes": ["sales", "marketing"],
  "language": "en",
  "timestamp": 1710000000
}
```

**Response:**
```json
{
  "success": true,
  "message": "Consent recorded successfully"
}
```

---

## Error Codes

| Code | Description |
|------|-------------|
| `NETWORK_ERROR` | No network connectivity |
| `TIMEOUT` | Request timed out |
| `SERVER_ERROR` | Server returned error (4xx/5xx) |
| `VALIDATION_ERROR` | Input validation failed |
| `NOT_INITIALIZED` | SDK not initialized |
| `ALREADY_VISIBLE` | Popup already visible |
| `UNKNOWN` | Unexpected error |
