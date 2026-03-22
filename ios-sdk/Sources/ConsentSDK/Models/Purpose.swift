import Foundation

/// Represents a single consent purpose fetched from the backend.
public struct Purpose: Codable, Identifiable, Equatable {
    /// Unique identifier for the purpose (e.g., "sales", "marketing")
    public let id: String
    /// Display name of the purpose
    public let name: String
    /// Human-readable description of what this purpose entails
    public let description: String
    /// Whether this purpose is required and cannot be deselected
    public let mandatory: Bool

    public init(id: String, name: String, description: String, mandatory: Bool = false) {
        self.id = id
        self.name = name
        self.description = description
        self.mandatory = mandatory
    }
}

/// Wrapper for the purposes API response.
struct PurposesResponse: Codable {
    let purposes: [Purpose]
}
