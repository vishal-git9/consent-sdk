const express = require('express');
const cors = require('cors');

const app = express();
app.use(cors());
app.use(express.json());

const PORT = process.env.PORT || 3000;

// ─── Mock Data ────────────────────────────────────────────────────

const purposesByLang = {
  en: [
    {
      id: 'sales',
      name: 'Sales',
      description: 'Allow contact from our sales team for product inquiries and offers.',
      mandatory: false,
    },
    {
      id: 'marketing',
      name: 'Marketing',
      description: 'Receive marketing emails, newsletters, and promotional content.',
      mandatory: false,
    },
    {
      id: 'services',
      name: 'Services',
      description: 'Required for core service delivery and customer support.',
      mandatory: true,
    },
    {
      id: 'it_support',
      name: 'IT Support',
      description: 'Allow IT team to access your account for troubleshooting and maintenance.',
      mandatory: false,
    },
    {
      id: 'analytics',
      name: 'Analytics',
      description: 'Collect usage data to improve our products and services.',
      mandatory: false,
    },
  ],
  fr: [
    {
      id: 'sales',
      name: 'Ventes',
      description: "Autoriser le contact de notre équipe commerciale pour les demandes de produits.",
      mandatory: false,
    },
    {
      id: 'marketing',
      name: 'Marketing',
      description: 'Recevoir des emails marketing, newsletters et contenu promotionnel.',
      mandatory: false,
    },
    {
      id: 'services',
      name: 'Services',
      description: 'Requis pour la prestation de services essentiels et le support client.',
      mandatory: true,
    },
    {
      id: 'it_support',
      name: 'Support Informatique',
      description: "Permettre à l'équipe informatique d'accéder à votre compte pour le dépannage.",
      mandatory: false,
    },
    {
      id: 'analytics',
      name: 'Analytique',
      description: "Collecter des données d'utilisation pour améliorer nos produits et services.",
      mandatory: false,
    },
  ],
  de: [
    {
      id: 'sales',
      name: 'Vertrieb',
      description: 'Kontaktaufnahme durch unser Vertriebsteam für Produktanfragen erlauben.',
      mandatory: false,
    },
    {
      id: 'marketing',
      name: 'Marketing',
      description: 'Marketing-E-Mails, Newsletter und Werbeinhalte erhalten.',
      mandatory: false,
    },
    {
      id: 'services',
      name: 'Dienste',
      description: 'Erforderlich für die Bereitstellung von Kerndienstleistungen und Kundensupport.',
      mandatory: true,
    },
    {
      id: 'it_support',
      name: 'IT-Support',
      description: 'Dem IT-Team den Zugriff auf Ihr Konto zur Fehlerbehebung erlauben.',
      mandatory: false,
    },
    {
      id: 'analytics',
      name: 'Analytik',
      description: 'Nutzungsdaten sammeln, um unsere Produkte und Dienste zu verbessern.',
      mandatory: false,
    },
  ],
};

// ─── Routes ───────────────────────────────────────────────────────

/**
 * GET /consent/purposes?lang=en
 *
 * Returns the list of consent purposes for the given language.
 * Query params:
 *   - lang (string): Language code. Defaults to "en".
 *   - simulate (string): Optional. "error" to simulate server error,
 *                         "empty" to return empty purposes,
 *                         "timeout" to simulate a timeout,
 *                         "slow" to simulate a slow response.
 */
app.get('/consent/purposes', (req, res) => {
  const lang = req.query.lang || 'en';
  const simulate = req.query.simulate;

  // Check auth header
  const authHeader = req.headers.authorization;
  if (authHeader && authHeader === 'Bearer INVALID_KEY') {
    return res.status(401).json({ error: 'Invalid API key' });
  }

  // Simulation modes
  if (simulate === 'error') {
    return res.status(500).json({ error: 'Internal server error' });
  }

  if (simulate === 'empty') {
    return res.json({ purposes: [] });
  }

  if (simulate === 'timeout') {
    // Don't respond — simulate timeout
    return;
  }

  if (simulate === 'slow') {
    return setTimeout(() => {
      const purposes = purposesByLang[lang] || purposesByLang.en;
      res.json({ purposes });
    }, 5000);
  }

  // Normal response
  const purposes = purposesByLang[lang] || purposesByLang.en;

  // Simulate slight network delay
  setTimeout(() => {
    res.json({ purposes });
  }, 300);
});

/**
 * POST /consent
 *
 * Receives the user's consent submission.
 * Body:
 *   - age (number): User's age
 *   - selectedPurposes (string[]): Selected purpose IDs
 *   - language (string): Current language code
 *   - timestamp (number): Submission timestamp
 */
app.post('/consent', (req, res) => {
  const { age, selectedPurposes, language, timestamp } = req.body;

  // Basic validation
  if (age == null || age === undefined) {
    return res.status(400).json({
      success: false,
      message: 'Age is required',
    });
  }

  if (!selectedPurposes || !Array.isArray(selectedPurposes)) {
    return res.status(400).json({
      success: false,
      message: 'selectedPurposes must be an array',
    });
  }

  // Check for simulate header
  const simulate = req.headers['x-simulate'];

  if (simulate === 'error') {
    return res.status(500).json({
      success: false,
      message: 'Internal server error',
    });
  }

  // Log the submission (for debugging)
  console.log('\n📋 Consent Submission Received:');
  console.log(`   Age: ${age}`);
  console.log(`   Purposes: ${selectedPurposes.join(', ')}`);
  console.log(`   Language: ${language}`);
  console.log(`   Timestamp: ${new Date(timestamp * 1000).toISOString()}`);

  // Simulate slight network delay
  setTimeout(() => {
    res.json({
      success: true,
      message: 'Consent recorded successfully',
    });
  }, 500);
});

/**
 * GET /health
 * Health check endpoint.
 */
app.get('/health', (req, res) => {
  res.json({ status: 'ok', timestamp: Date.now() });
});

// ─── Start Server ─────────────────────────────────────────────────

app.listen(PORT, () => {
  console.log(`\n🚀 Consent SDK Mock Server running on http://localhost:${PORT}`);
  console.log(`\n📡 Available endpoints:`);
  console.log(`   GET  /consent/purposes?lang=en`);
  console.log(`   POST /consent`);
  console.log(`   GET  /health`);
  console.log(`\n🧪 Simulation modes (via ?simulate= query param):`);
  console.log(`   error   — Returns 500 server error`);
  console.log(`   empty   — Returns empty purposes list`);
  console.log(`   timeout — Simulates request timeout (never responds)`);
  console.log(`   slow    — Responds after 5 second delay`);
  console.log(`\n🌍 Supported languages: en, fr, de\n`);
});
