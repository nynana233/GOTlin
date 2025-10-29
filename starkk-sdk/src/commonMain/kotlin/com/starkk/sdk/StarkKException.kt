package com.starkk.sdk

/**
 * Sealed exception hierarchy for all errors surfaced by the StarkK SDK.
 *
 * Consumers handle errors via
 * [StarkKPageResult.onFailure][com.starkk.sdk.models.StarkKPageResult.onFailure]
 * without needing to know about HTTP internals, Ktor, or network-layer details.
 */
sealed class StarkKException(
    override val message: String,
    override val cause: Throwable? = null,
) : Exception(message, cause) {

    /**
     * The server returned a non-2xx HTTP status code.
     *
     * @property code The HTTP status code (e.g. 404, 500).
     */
    class HttpError(
        val code: Int,
        message: String,
    ) : StarkKException("HTTP $code: $message")

    /**
     * A network-level failure (e.g. no connectivity, DNS resolution, timeout).
     */
    class NetworkError(
        cause: Throwable,
    ) : StarkKException(cause.message ?: "Network error", cause)

    /**
     * An unexpected error that does not fit into the other categories.
     */
    class UnknownError(
        cause: Throwable,
    ) : StarkKException(cause.message ?: "Unknown error", cause)
}

