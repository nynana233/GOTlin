package com.starkk.sdk.models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PaginatedResultTest {

    @Test
    fun hasNextPageWhenNextIsNotNull() {
        val result = PaginatedResult(
            data = listOf(1, 2, 3),
            next = "https://example.com/page=2",
        )
        assertTrue(result.hasNextPage)
    }

    @Test
    fun hasNextPageIsFalseWhenNextIsNull() {
        val result = PaginatedResult(
            data = listOf(1, 2, 3),
            next = null,
        )
        assertFalse(result.hasNextPage)
    }

    @Test
    fun hasPrevPageWhenPrevIsNotNull() {
        val result = PaginatedResult(
            data = listOf(1, 2, 3),
            prev = "https://example.com/page=1",
        )
        assertTrue(result.hasPrevPage)
    }

    @Test
    fun hasPrevPageIsFalseWhenPrevIsNull() {
        val result = PaginatedResult(
            data = listOf(1, 2, 3),
            prev = null,
        )
        assertFalse(result.hasPrevPage)
    }

    @Test
    fun storesAllPaginationUrls() {
        val nextUrl = "https://example.com/page=2"
        val prevUrl = "https://example.com/page=1"
        val firstUrl = "https://example.com/page=1"
        val lastUrl = "https://example.com/page=100"

        val result = PaginatedResult(
            data = listOf("item1", "item2"),
            next = nextUrl,
            prev = prevUrl,
            first = firstUrl,
            last = lastUrl,
        )

        assertEquals(nextUrl, result.next)
        assertEquals(prevUrl, result.prev)
        assertEquals(firstUrl, result.first)
        assertEquals(lastUrl, result.last)
    }

    @Test
    fun emptyDataListIsValid() {
        val result = PaginatedResult(
            data = emptyList<Int>(),
            next = null,
        )

        assertTrue(result.data.isEmpty())
        assertFalse(result.hasNextPage)
    }
}

