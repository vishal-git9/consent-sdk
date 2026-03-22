import Foundation

/// API client for the consent backend.
///
/// Handles fetching purposes and submitting consent using URLSession.
final class ConsentApiClient {

    private let baseUrl: String
    private let apiKey: String
    private let session: URLSession

    init(baseUrl: String, apiKey: String, timeoutInterval: TimeInterval = 15) {
        self.baseUrl = baseUrl
        self.apiKey = apiKey

        let config = URLSessionConfiguration.default
        config.timeoutIntervalForRequest = timeoutInterval
        config.timeoutIntervalForResource = timeoutInterval * 2
        self.session = URLSession(configuration: config)
    }

    // MARK: - Fetch Purposes

    /// Fetches consent purposes for the given language.
    func fetchPurposes(language: String) async -> ConsentResult<[Purpose]> {
        guard var urlComponents = URLComponents(string: "\(baseUrl)/consent/purposes") else {
            return .error(message: "Invalid base URL", code: .serverError)
        }
        urlComponents.queryItems = [URLQueryItem(name: "lang", value: language)]

        guard let url = urlComponents.url else {
            return .error(message: "Invalid URL", code: .serverError)
        }

        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.setValue("Bearer \(apiKey)", forHTTPHeaderField: "Authorization")
        request.setValue("application/json", forHTTPHeaderField: "Accept")

        do {
            let (data, response) = try await session.data(for: request)

            guard let httpResponse = response as? HTTPURLResponse else {
                return .error(message: "Invalid server response", code: .serverError)
            }

            switch httpResponse.statusCode {
            case 200:
                let decoded = try JSONDecoder().decode(PurposesResponse.self, from: data)
                return .success(decoded.purposes)
            case 401:
                return .error(message: "Invalid API key", code: .serverError)
            default:
                return .error(
                    message: "Server error: \(httpResponse.statusCode)",
                    code: .serverError
                )
            }
        } catch let error as URLError where error.code == .timedOut {
            return .error(
                message: "Request timed out. Please check your connection.",
                code: .timeout
            )
        } catch let error as URLError {
            return .error(
                message: "Network error: \(error.localizedDescription)",
                code: .networkError
            )
        } catch {
            return .error(
                message: "Error: \(error.localizedDescription)",
                code: .unknown
            )
        }
    }

    // MARK: - Submit Consent

    /// Submits the user's consent choices.
    func submitConsent(
        age: Int,
        selectedPurposes: [String],
        language: String
    ) async -> ConsentResult<Bool> {
        guard let url = URL(string: "\(baseUrl)/consent") else {
            return .error(message: "Invalid base URL", code: .serverError)
        }

        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("Bearer \(apiKey)", forHTTPHeaderField: "Authorization")
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")

        let body: [String: Any] = [
            "age": age,
            "selectedPurposes": selectedPurposes,
            "language": language,
            "timestamp": Int(Date().timeIntervalSince1970)
        ]

        do {
            request.httpBody = try JSONSerialization.data(withJSONObject: body)
            let (data, response) = try await session.data(for: request)

            guard let httpResponse = response as? HTTPURLResponse else {
                return .error(message: "Invalid server response", code: .serverError)
            }

            switch httpResponse.statusCode {
            case 200, 201:
                return .success(true)
            case 400:
                return .error(message: "Invalid consent data submitted", code: .validationError)
            case 401:
                return .error(message: "Invalid API key", code: .serverError)
            default:
                return .error(
                    message: "Server error: \(httpResponse.statusCode)",
                    code: .serverError
                )
            }
        } catch let error as URLError where error.code == .timedOut {
            return .error(
                message: "Request timed out. Please check your connection.",
                code: .timeout
            )
        } catch let error as URLError {
            return .error(
                message: "Network error: \(error.localizedDescription)",
                code: .networkError
            )
        } catch {
            return .error(
                message: "Error: \(error.localizedDescription)",
                code: .unknown
            )
        }
    }
}
