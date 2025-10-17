package com.starkk.sdk.models

/**
 * A generic wrapper for paginated API responses.
 *
 * Holds the deserialized data along with parsed RFC 5988 `Link` header URLs
 * for navigating between pages of results.
 *
 * @param T The type of items in the result list.
 * @property data The list of deserialized items for the current page.
 * @property next URL for the next page, or `null` if this is the last page.
 * @property prev URL for the previous page, or `null` if this is the first page.
 * @property first URL for the first page, or `null` if not provided.
 * @property last URL for the last page, or `null` if not provided.
 */
internal data class PaginatedResult<T>(
    val data: List<T>,
    val next: String? = null,
    val prev: String? = null,
    val first: String? = null,
    val last: String? = null,
) {
    /** Returns `true` if there is a next page available. */
    val hasNextPage: Boolean get() = next != null

    /** Returns `true` if there is a previous page available. */
    val hasPrevPage: Boolean get() = prev != null
}

