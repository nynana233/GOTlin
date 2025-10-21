package com.starkk.sdk.network

import io.ktor.http.Headers
import io.ktor.http.headersOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PaginationParserTest {

    @Test
    fun parseValidLinkHeaderWithAllRelations() {
        val linkHeader = """<https://anapioficeandfire.com/api/characters?page=2&pageSize=10>; rel="next",<https://anapioficeandfire.com/api/characters?page=1&pageSize=10>; rel="prev",<https://anapioficeandfire.com/api/characters?page=1&pageSize=10>; rel="first",<https://anapioficeandfire.com/api/characters?page=214&pageSize=10>; rel="last""""

        val headers = headersOf("Link", linkHeader)
        val result = PaginationParser.parse(listOf(1, 2, 3), headers)

        assertEquals("https://anapioficeandfire.com/api/characters?page=2&pageSize=10", result.next)
        assertEquals("https://anapioficeandfire.com/api/characters?page=1&pageSize=10", result.prev)
        assertEquals("https://anapioficeandfire.com/api/characters?page=1&pageSize=10", result.first)
        assertEquals("https://anapioficeandfire.com/api/characters?page=214&pageSize=10", result.last)
    }

    @Test
    fun parsePartialLinkHeader() {
        val linkHeader = """<https://anapioficeandfire.com/api/characters?page=2&pageSize=10>; rel="next""""

        val headers = headersOf("Link", linkHeader)
        val result = PaginationParser.parse(listOf(1, 2, 3), headers)

        assertEquals("https://anapioficeandfire.com/api/characters?page=2&pageSize=10", result.next)
        assertNull(result.prev)
        assertNull(result.first)
        assertNull(result.last)
    }

    @Test
    fun parseWithoutLinkHeader() {
        val headers = headersOf()
        val result = PaginationParser.parse(listOf(1, 2, 3), headers)

        assertTrue(result.data.size == 3)
        assertNull(result.next)
        assertNull(result.prev)
        assertNull(result.first)
        assertNull(result.last)
    }

    @Test
    fun parseWithEmptyData() {
        val linkHeader = """<https://example.com/page=2>; rel="next""""

        val headers = headersOf("Link", linkHeader)
        val result = PaginationParser.parse(emptyList<Int>(), headers)

        assertTrue(result.data.isEmpty())
        assertEquals("https://example.com/page=2", result.next)
    }

    @Test
    fun parseLinkHeaderWithExtraWhitespace() {
        val linkHeader = """<https://example.com/page=2>  ;  rel="next"  ,  <https://example.com/page=1>  ;  rel="prev""""

        val headers = headersOf("Link", linkHeader)
        val result = PaginationParser.parse(listOf("item1"), headers)

        assertEquals("https://example.com/page=2", result.next)
        assertEquals("https://example.com/page=1", result.prev)
    }

    @Test
    fun parseLinkHeaderWithQueryParameters() {
        val linkHeader = """<https://example.com/api?page=2&pageSize=50&filter=name>; rel="next""""

        val headers = headersOf("Link", linkHeader)
        val result = PaginationParser.parse(listOf("item"), headers)

        assertEquals("https://example.com/api?page=2&pageSize=50&filter=name", result.next)
    }
}

