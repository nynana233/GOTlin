package com.starkk.sdk.models

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HouseDeserializationTest {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    @Test
    fun deserializeFullHouse() {
        val payload = """
            {
              "url": "https://anapioficeandfire.com/api/houses/1",
              "name": "House Stark",
              "region": "The North",
              "coatOfArms": "A grey direwolf on a white field.",
              "words": "Winter is Coming",
              "titles": ["Warden of the North"],
              "seats": ["Winterfell"],
              "currentLord": "https://anapioficeandfire.com/api/characters/1",
              "heir": "https://anapioficeandfire.com/api/characters/2",
              "overlord": "",
              "founded": "8,000 BC",
              "founder": "Brandon the Breaker",
              "diedOut": "",
              "ancestralWeapons": ["Ice"],
              "cadetBranches": [],
              "swornMembers": []
            }
        """.trimIndent()

        val house = json.decodeFromString<House>(payload)

        assertEquals("House Stark", house.name)
        assertEquals("The North", house.region)
        assertEquals("Winter is Coming", house.words)
        assertTrue(house.titles.contains("Warden of the North"))
        assertTrue(house.seats.contains("Winterfell"))
        assertTrue(house.ancestralWeapons.contains("Ice"))
    }

    @Test
    fun deserializeHouseWithMissingOptionalFields() {
        val payload = """
            {
              "url": "https://anapioficeandfire.com/api/houses/1",
              "name": "House Stark"
            }
        """.trimIndent()

        val house = json.decodeFromString<House>(payload)

        assertEquals("House Stark", house.name)
        assertEquals("", house.region)
        assertEquals("", house.coatOfArms)
        assertEquals("", house.words)
        assertTrue(house.titles.isEmpty())
        assertTrue(house.seats.isEmpty())
        assertTrue(house.ancestralWeapons.isEmpty())
    }

    @Test
    fun deserializeHouseWithEmptyCollections() {
        val payload = """
            {
              "url": "https://anapioficeandfire.com/api/houses/1",
              "name": "House Stark",
              "titles": [],
              "seats": [],
              "ancestralWeapons": [],
              "cadetBranches": [],
              "swornMembers": []
            }
        """.trimIndent()

        val house = json.decodeFromString<House>(payload)

        assertTrue(house.titles.isEmpty())
        assertTrue(house.seats.isEmpty())
        assertTrue(house.ancestralWeapons.isEmpty())
        assertTrue(house.cadetBranches.isEmpty())
        assertTrue(house.swornMembers.isEmpty())
    }
}

