package com.starkk.sdk.models

import com.starkk.sdk.StarkKException

/**
 * A page of results from the StarkK SDK.
 *
 * Holds the deserialized items along with pagination state.
 * Consumers navigate between pages using the type-specific
 * `nextXxx` / `previousXxx` methods on [com.starkk.sdk.StarkKClient].
 *
 * @param T The type of items in this page.
 * @property items The list of items for the current page.
 * @property hasNext `true` if there is a next page available.
 * @property hasPrevious `true` if there is a previous page available.
 * @property currentPage The 1-based page number for the current page.
 */
class StarkKPage<T> internal constructor(
    val items: List<T>,
    val hasNext: Boolean,
    val hasPrevious: Boolean,
    val currentPage: Int,
    @PublishedApi internal val nextUrl: String?,
    @PublishedApi internal val prevUrl: String?,
) {

    internal companion object {

        /**
         * Regex to extract the `page` query parameter value from a URL.
         */
        private val PAGE_REGEX = Regex("""[?&]page=(\d+)""")

        /**
         * Converts an internal [PaginatedResult] into a public [StarkKPage].
         *
         * @param result The internal paginated result from the API layer.
         * @param requestedPage Fallback page number when the URL cannot be parsed
         *                      (e.g. the very first request where no Link header exists).
         */
        fun <T> from(result: PaginatedResult<T>, requestedPage: Int = 1): StarkKPage<T> {
            val currentPage = parsePage(result) ?: requestedPage
            return StarkKPage(
                items = result.data,
                hasNext = result.hasNextPage,
                hasPrevious = result.hasPrevPage,
                currentPage = currentPage,
                nextUrl = result.next,
                prevUrl = result.prev,
            )
        }

        /**
         * Parses the current page number from the Link header URLs.
         *
         * Strategy: if a `next` URL exists, current page = next − 1.
         * Otherwise if a `prev` URL exists, current page = prev + 1.
         * Falls back to `null` if neither can be parsed.
         */
        private fun <T> parsePage(result: PaginatedResult<T>): Int? {
            result.next?.let { url ->
                val nextPage = PAGE_REGEX.find(url)?.groupValues?.get(1)?.toIntOrNull()
                if (nextPage != null) return nextPage - 1
            }
            result.prev?.let { url ->
                val prevPage = PAGE_REGEX.find(url)?.groupValues?.get(1)?.toIntOrNull()
                if (prevPage != null) return prevPage + 1
            }
            return null
        }
    }
}

/**
 * Represents the outcome of a StarkK SDK call that returns a page of results.
 *
 * Use [onSuccess] and [onFailure] for idiomatic chained handling:
 *
 * ```kotlin
 * client.getCharacters(page = 1)
 *     .onSuccess { page -> /* use page.items */ }
 *     .onFailure { error -> /* handle StarkKException */ }
 * ```
 */
sealed class StarkKPageResult<out T> {

    /**
     * The call succeeded and produced a [StarkKPage].
     */
    class Success<T>(val page: StarkKPage<T>) : StarkKPageResult<T>()

    /**
     * The call failed with a [StarkKException].
     */
    class Failure(val exception: StarkKException) : StarkKPageResult<Nothing>()

    /**
     * Calls [action] if this is a [Success], passing the [StarkKPage].
     * Returns `this` for chaining.
     */
    inline fun onSuccess(action: (StarkKPage<@UnsafeVariance T>) -> Unit): StarkKPageResult<T> {
        if (this is Success) action(page)
        return this
    }

    /**
     * Calls [action] if this is a [Failure], passing the [StarkKException].
     * Returns `this` for chaining.
     */
    inline fun onFailure(action: (StarkKException) -> Unit): StarkKPageResult<T> {
        if (this is Failure) action(exception)
        return this
    }
}

