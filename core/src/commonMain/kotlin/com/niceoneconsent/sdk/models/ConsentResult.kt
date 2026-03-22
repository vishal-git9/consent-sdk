package com.niceoneconsent.sdk.models

/**
 * Sealed class representing the result of an SDK operation.
 */
sealed class ConsentResult<out T> {
    data class Success<T>(val data: T) : ConsentResult<T>()
    data class Error(
        val message: String,
        val code: ErrorCode = ErrorCode.UNKNOWN,
        val cause: Throwable? = null
    ) : ConsentResult<Nothing>()
}

/**
 * Error codes for consent operations.
 */
enum class ErrorCode {
    NETWORK_ERROR,
    TIMEOUT,
    SERVER_ERROR,
    VALIDATION_ERROR,
    NOT_INITIALIZED,
    ALREADY_VISIBLE,
    UNKNOWN
}
