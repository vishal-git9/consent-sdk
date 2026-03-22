import Foundation

/// Analytics event types emitted by the Consent SDK.
public enum ConsentEvent {
    case consentShown(language: String)
    case consentSubmitted(selectedPurposes: [String], age: Int, language: String)
    case consentDeclined(language: String)
    case consentDismissed
    case languageChanged(from: String, to: String)
    case error(message: String, code: String)
}

/// Protocol for receiving consent analytics events.
public protocol ConsentEventListener: AnyObject {
    func onEvent(_ event: ConsentEvent)
}

/// Manages analytics event listeners and dispatches events.
public class ConsentAnalytics {
    private var listeners: [ConsentEventListener] = []
    private let lock = NSLock()

    public func addListener(_ listener: ConsentEventListener) {
        lock.lock()
        defer { lock.unlock() }
        listeners.append(listener)
    }

    public func removeListener(_ listener: ConsentEventListener) {
        lock.lock()
        defer { lock.unlock() }
        listeners.removeAll { $0 === listener }
    }

    public func clearListeners() {
        lock.lock()
        defer { lock.unlock() }
        listeners.removeAll()
    }

    internal func dispatch(_ event: ConsentEvent) {
        let snapshot: [ConsentEventListener]
        lock.lock()
        snapshot = listeners
        lock.unlock()
        snapshot.forEach { $0.onEvent(event) }
    }
}
