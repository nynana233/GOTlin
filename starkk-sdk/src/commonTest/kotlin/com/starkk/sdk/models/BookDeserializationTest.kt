package com.starkk.sdk.models

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BookDeserializationTest {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    @Test
    fun deserializeFullBook() {
        val payload = """
            {
              "url": "https://anapioficeandfire.com/api/books/1",
              "name": "A Game of Thrones",
              "isbn": "978-0553103540",
              "authors": ["George R. R. Martin"],
              "numberOfPages": 694,
              "publisher": "Bantam Books",
              "country": "United States",
              "mediaType": "Hardback",
              "released": "1996-08-01",
              "characters": ["https://anapioficeandfire.com/api/characters/1"],
              "povCharacters": ["https://anapioficeandfire.com/api/characters/1"]
            }
        """.trimIndent()

        val book = json.decodeFromString<Book>(payload)

        assertEquals("A Game of Thrones", book.name)
        assertEquals("978-0553103540", book.isbn)
        assertEquals(694, book.numberOfPages)
        assertEquals("George R. R. Martin", book.authors[0])
        assertEquals("1996-08-01", book.released)
    }

    @Test
    fun deserializeBookWithMissingOptionalFields() {
        val payload = """
            {
              "url": "https://anapioficeandfire.com/api/books/1",
              "name": "A Game of Thrones"
            }
        """.trimIndent()

        val book = json.decodeFromString<Book>(payload)

        assertEquals("A Game of Thrones", book.name)
        assertEquals("", book.isbn)
        assertEquals("", book.publisher)
        assertEquals("", book.country)
        assertEquals(0, book.numberOfPages)
        assertTrue(book.authors.isEmpty())
        assertTrue(book.characters.isEmpty())
        assertTrue(book.povCharacters.isEmpty())
    }

    @Test
    fun deserializeBookWithEmptyCollections() {
        val payload = """
            {
              "url": "https://anapioficeandfire.com/api/books/1",
              "name": "A Game of Thrones",
              "authors": [],
              "characters": [],
              "povCharacters": []
            }
        """.trimIndent()

        val book = json.decodeFromString<Book>(payload)

        assertTrue(book.authors.isEmpty())
        assertTrue(book.characters.isEmpty())
        assertTrue(book.povCharacters.isEmpty())
    }
}

