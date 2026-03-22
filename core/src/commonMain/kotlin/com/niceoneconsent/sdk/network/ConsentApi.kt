package com.niceoneconsent.sdk.network

import com.niceoneconsent.sdk.models.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

/**
 * API client for the consent backend.
 *
 * Handles fetching purposes and submitting consent.
 * All methods are suspending and should be called from a coroutine scope.
 */
class ConsentApi(
    private val client: HttpClient,
    private val baseUrl: String,
    private val apiKey: String
) {
    /**
     * Fetches the list of consent purposes for the given language.
     *
     * @param language Language code (e.g., "en", "fr")
     * @return [ConsentResult] containing [PurposesResponse] on success
     */
    suspend fun fetchPurposes(language: String): ConsentResult<PurposesResponse> {
        return try {
            val response: HttpResponse = client.get("$baseUrl/consent/purposes") {
                parameter("lang", language)
                header("Authorization", "Bearer $apiKey")
                header("Accept", "application/json")
            }

            when (response.status) {
                HttpStatusCode.OK -> {
                    val body = response.body<PurposesResponse>()
                    ConsentResult.Success(body)
                }
                HttpStatusCode.Unauthorized -> {
                    ConsentResult.Error(
                        message = "Invalid API key",
                        code = ErrorCode.SERVER_ERROR
                    )
                }
                else -> {
                    ConsentResult.Error(
                        message = "Server error: ${response.status.value}",
                        code = ErrorCode.SERVER_ERROR
                    )
                }
            }
        } catch (e: io.ktor.client.plugins.HttpRequestTimeoutException) {
            ConsentResult.Error(
                message = "Request timed out. Please check your connection.",
                code = ErrorCode.TIMEOUT,
                cause = e
            )
        } catch (e: Exception) {
            ConsentResult.Error(
                message = "Network error: ${e.message ?: "Unknown error"}",
                code = ErrorCode.NETWORK_ERROR,
                cause = e
            )
        }
    }

    /**
     * Submits the user's consent choices to the backend.
     *
     * @param request The consent submission request body
     * @return [ConsentResult] containing [ConsentResponse] on success
     */
    suspend fun submitConsent(request: ConsentRequest): ConsentResult<ConsentResponse> {
        return try {
            val response: HttpResponse = client.post("$baseUrl/consent") {
                header("Authorization", "Bearer $apiKey")
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            when (response.status) {
                HttpStatusCode.OK, HttpStatusCode.Created -> {
                    val body = response.body<ConsentResponse>()
                    ConsentResult.Success(body)
                }
                HttpStatusCode.BadRequest -> {
                    ConsentResult.Error(
                        message = "Invalid consent data submitted",
                        code = ErrorCode.VALIDATION_ERROR
                    )
                }
                HttpStatusCode.Unauthorized -> {
                    ConsentResult.Error(
                        message = "Invalid API key",
                        code = ErrorCode.SERVER_ERROR
                    )
                }
                else -> {
                    ConsentResult.Error(
                        message = "Server error: ${response.status.value}",
                        code = ErrorCode.SERVER_ERROR
                    )
                }
            }
        } catch (e: io.ktor.client.plugins.HttpRequestTimeoutException) {
            ConsentResult.Error(
                message = "Request timed out. Please check your connection.",
                code = ErrorCode.TIMEOUT,
                cause = e
            )
        } catch (e: Exception) {
            ConsentResult.Error(
                message = "Network error: ${e.message ?: "Unknown error"}",
                code = ErrorCode.NETWORK_ERROR,
                cause = e
            )
        }
    }

    /**
     * Closes the underlying HTTP client.
     */
    fun close() {
        client.close()
    }
}
