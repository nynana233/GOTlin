package com.starkk.sdk

import com.starkk.sdk.models.Character
import com.starkk.sdk.models.House
import com.starkk.sdk.models.StarkKPageResult
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class StarkKClientTest {

    private val testJson = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    private fun createMockClient(
        responseBody: String = "[]",
        statusCode: HttpStatusCode = HttpStatusCode.OK,
        linkHeader: String? = null,
    ): StarkKClient {
        val mockEngine = MockEngine { _ ->
            val headers = buildMap {
                put(HttpHeaders.ContentType, listOf(ContentType.Application.Json.toString()))
                if (linkHeader != null) {
                    put("Link", listOf(linkHeader))
                }
            }
            respond(
                content = responseBody,
                status = statusCode,
                headers = headersOf(*headers.map { (k, v) -> k to v }.toTypedArray()),
            )
        }

        val httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(testJson)
            }
        }

        return StarkKClient.Builder()
            .httpClient(httpClient)
            .build()
    }

    @Test
    fun successfulCharacterFetchReturnsSuccess() = runTest {
        val characters = listOf(
            Character(name = "Jon Snow", gender = "Male"),
            Character(name = "Daenerys Targaryen", gender = "Female"),
        )
        val client = createMockClient(
            responseBody = testJson.encodeToString(characters),
        )

        val result = client.getCharacters(page = 1, pageSize = 10)

        assertIs<StarkKPageResult.Success<Character>>(result)
        assertEquals(2, result.page.items.size)
        assertEquals("Jon Snow", result.page.items[0].name)
        assertEquals("Daenerys Targaryen", result.page.items[1].name)
    }

    @Test
    fun paginationHeadersAreParsedIntoPage() = runTest {
        val characters = listOf(Character(name = "Jon Snow"))
        val client = createMockClient(
            responseBody = testJson.encodeToString(characters),
            linkHeader = """<https://example.com/api/characters?page=2&pageSize=10>; rel="next"""",
        )

        val result = client.getCharacters(page = 1, pageSize = 10)

        assertIs<StarkKPageResult.Success<Character>>(result)
        assertTrue(result.page.hasNext)
        assertEquals(1, result.page.currentPage)
    }

    @Test
    fun httpErrorIsWrappedInFailure() = runTest {
        val client = createMockClient(
            responseBody = "Server Error",
            statusCode = HttpStatusCode.InternalServerError,
        )

        val result = client.getCharacters(page = 1, pageSize = 10)

        assertIs<StarkKPageResult.Failure>(result)
        assertIs<StarkKException.HttpError>(result.exception)
        assertEquals(500, (result.exception as StarkKException.HttpError).code)
    }

    @Test
    fun getCharactersByNameReturnsResults() = runTest {
        val characters = listOf(Character(name = "Jon Snow"))
        val client = createMockClient(
            responseBody = testJson.encodeToString(characters),
        )

        val result = client.getCharactersByName("Jon Snow", page = 1, pageSize = 10)

        assertIs<StarkKPageResult.Success<Character>>(result)
        assertEquals(1, result.page.items.size)
    }

    @Test
    fun getHousesReturnsHouses() = runTest {
        val houses = listOf(
            House(name = "House Stark", region = "The North"),
            House(name = "House Lannister", region = "The Westerlands"),
        )
        val client = createMockClient(
            responseBody = testJson.encodeToString(houses),
        )

        val result = client.getHouses(page = 1, pageSize = 10)

        assertIs<StarkKPageResult.Success<House>>(result)
        val items = result.page.items
        assertEquals(2, items.size)
        assertEquals("House Stark", items[0].name)
    }

    @Test
    fun getBooksReturnsBooks() = runTest {
        val client = createMockClient(responseBody = "[]")

        val result = client.getBooks(page = 1, pageSize = 10)

        assertIs<StarkKPageResult.Success<com.starkk.sdk.models.Book>>(result)
        assertTrue(result.page.items.isEmpty())
    }

    @Test
    fun emptyResponseBodyHandled() = runTest {
        val client = createMockClient(responseBody = "[]")

        val result = client.getCharacters(page = 1, pageSize = 10)

        assertIs<StarkKPageResult.Success<Character>>(result)
        assertTrue(result.page.items.isEmpty())
    }

    @Test
    fun onSuccessChainIsCalledForSuccess() = runTest {
        val characters = listOf(Character(name = "Arya Stark"))
        val client = createMockClient(
            responseBody = testJson.encodeToString(characters),
        )

        var captured: List<Character>? = null
        client.getCharacters(page = 1, pageSize = 10)
            .onSuccess { page -> captured = page.items }
            .onFailure { /* should not be called */ }

        assertNotNull(captured)
        assertEquals("Arya Stark", captured!![0].name)
    }

    @Test
    fun onFailureChainIsCalledForError() = runTest {
        val client = createMockClient(
            responseBody = "Server Error",
            statusCode = HttpStatusCode.InternalServerError,
        )

        var captured: StarkKException? = null
        client.getCharacters(page = 1, pageSize = 10)
            .onSuccess { /* should not be called */ }
            .onFailure { e -> captured = e }

        assertNotNull(captured)
        assertIs<StarkKException.HttpError>(captured)
    }

    @Test
    fun nextCharactersReturnsNullWhenNoNextPage() = runTest {
        val characters = listOf(Character(name = "Jon Snow"))
        val client = createMockClient(
            responseBody = testJson.encodeToString(characters),
        )

        val result = client.getCharacters(page = 1, pageSize = 10)
        assertIs<StarkKPageResult.Success<Character>>(result)

        val next = client.nextCharacters(result.page)
        assertNull(next)
    }

    @Test
    fun currentPageIsParsedFromLinkHeader() = runTest {
        val characters = listOf(Character(name = "Jon Snow"))
        val client = createMockClient(
            responseBody = testJson.encodeToString(characters),
            linkHeader = """<https://example.com/api/characters?page=3&pageSize=10>; rel="next"""",
        )

        val result = client.getCharacters(page = 2, pageSize = 10)
        assertIs<StarkKPageResult.Success<Character>>(result)
        assertEquals(2, result.page.currentPage)
    }

    @Test
    fun builderCreatesClientWithDefaults() {
        val client = StarkKClient.Builder().build()
        assertNotNull(client)
        client.close()
    }

    @Test
    fun builderAllowsCustomBaseUrl() {
        val client = StarkKClient.Builder()
            .baseUrl("https://custom.example.com/api")
            .build()
        assertNotNull(client)
        client.close()
    }

    @Test
    fun builderAllowsLogging() {
        val client = StarkKClient.Builder()
            .enableLogging(true)
            .build()
        assertNotNull(client)
        client.close()
    }

    @Test
    fun builderAllowsTimeouts() {
        val client = StarkKClient.Builder()
            .connectTimeout(60)
            .requestTimeout(60)
            .build()
        assertNotNull(client)
        client.close()
    }
}

