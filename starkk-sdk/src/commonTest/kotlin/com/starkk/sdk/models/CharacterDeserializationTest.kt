package com.starkk.sdk.models

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CharacterDeserializationTest {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    @Test
    fun deserializeFullCharacter() {
        val payload = """
            {
              "url": "https://anapioficeandfire.com/api/characters/1",
              "name": "Jon Snow",
              "gender": "Male",
              "culture": "Northmen",
              "born": "283 AC",
              "died": "",
              "titles": ["Lord Commander of the Night's Watch"],
              "aliases": ["The White Wolf"],
              "father": "",
              "mother": "",
              "spouse": "",
              "allegiances": ["https://anapioficeandfire.com/api/houses/20"],
              "books": ["https://anapioficeandfire.com/api/books/1"],
              "povBooks": ["https://anapioficeandfire.com/api/books/1"],
              "tvSeries": ["Season 1", "Season 2"],
              "playedBy": ["Kit Harington"]
            }
        """.trimIndent()

        val character = json.decodeFromString<Character>(payload)

        assertEquals("Jon Snow", character.name)
        assertEquals("Male", character.gender)
        assertEquals("Northmen", character.culture)
        assertEquals("283 AC", character.born)
        assertTrue(character.titles.contains("Lord Commander of the Night's Watch"))
        assertTrue(character.aliases.contains("The White Wolf"))
        assertEquals(1, character.allegiances.size)
        assertEquals(1, character.playedBy.size)
    }

    @Test
    fun deserializeCharacterWithMissingOptionalFields() {
        val payload = """
            {
              "url": "https://anapioficeandfire.com/api/characters/1",
              "name": "Jon Snow"
            }
        """.trimIndent()

        val character = json.decodeFromString<Character>(payload)

        assertEquals("Jon Snow", character.name)
        assertEquals("", character.gender)
        assertEquals("", character.culture)
        assertEquals("", character.born)
        assertEquals("", character.died)
        assertTrue(character.titles.isEmpty())
        assertTrue(character.aliases.isEmpty())
        assertTrue(character.books.isEmpty())
        assertTrue(character.povBooks.isEmpty())
    }

    @Test
    fun deserializeCharacterWithUnknownFields() {
        val payload = """
            {
              "url": "https://anapioficeandfire.com/api/characters/1",
              "name": "Jon Snow",
              "unknownField": "should be ignored",
              "anotherUnknown": 12345
            }
        """.trimIndent()

        val character = json.decodeFromString<Character>(payload)

        assertEquals("Jon Snow", character.name)
    }

    @Test
    fun deserializeCharacterWithEmptyCollections() {
        val payload = """
            {
              "url": "https://anapioficeandfire.com/api/characters/1",
              "name": "Character",
              "titles": [],
              "aliases": [],
              "books": [],
              "povBooks": [],
              "tvSeries": [],
              "playedBy": []
            }
        """.trimIndent()

        val character = json.decodeFromString<Character>(payload)

        assertTrue(character.titles.isEmpty())
        assertTrue(character.aliases.isEmpty())
        assertTrue(character.books.isEmpty())
        assertTrue(character.povBooks.isEmpty())
        assertTrue(character.tvSeries.isEmpty())
        assertTrue(character.playedBy.isEmpty())
    }
}

