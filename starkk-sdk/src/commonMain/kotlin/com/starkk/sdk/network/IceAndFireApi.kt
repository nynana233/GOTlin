package com.starkk.sdk.network

import com.starkk.sdk.models.Book
import com.starkk.sdk.models.Character
import com.starkk.sdk.models.House
import com.starkk.sdk.models.PaginatedResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse

/**
 * Internal Ktor-based API client for "An API of Ice And Fire".
 *
 * All endpoints return [PaginatedResult] wrappers that include
 * both the deserialized body and parsed `Link` header pagination URLs.
 */
internal class IceAndFireApi(
    private val httpClient: HttpClient,
    private val baseUrl: String,
) {

    // ── Characters ──────────────────────────────────────────────

    suspend fun getCharacters(
        page: Int = 1,
        pageSize: Int = 10,
    ): PaginatedResult<Character> = getPaginated("$baseUrl/characters") {
        parameter("page", page)
        parameter("pageSize", pageSize)
    }

    suspend fun getCharactersByName(
        name: String,
        page: Int = 1,
        pageSize: Int = 10,
    ): PaginatedResult<Character> = getPaginated("$baseUrl/characters") {
        parameter("name", name)
        parameter("page", page)
        parameter("pageSize", pageSize)
    }

    suspend fun getCharactersByQuery(
        name: String? = null,
        gender: String? = null,
        culture: String? = null,
        born: String? = null,
        died: String? = null,
        isAlive: Boolean? = null,
        page: Int = 1,
        pageSize: Int = 10,
    ): PaginatedResult<Character> = getPaginated("$baseUrl/characters") {
        name?.let { parameter("name", it) }
        gender?.let { parameter("gender", it) }
        culture?.let { parameter("culture", it) }
        born?.let { parameter("born", it) }
        died?.let { parameter("died", it) }
        isAlive?.let { parameter("isAlive", it) }
        parameter("page", page)
        parameter("pageSize", pageSize)
    }

    suspend fun getCharactersByUrl(
        url: String,
    ): PaginatedResult<Character> = getPaginated(url)

    // ── Houses ──────────────────────────────────────────────────

    suspend fun getHouses(
        page: Int = 1,
        pageSize: Int = 10,
    ): PaginatedResult<House> = getPaginated("$baseUrl/houses") {
        parameter("page", page)
        parameter("pageSize", pageSize)
    }

    suspend fun getHousesByName(
        name: String,
        page: Int = 1,
        pageSize: Int = 10,
    ): PaginatedResult<House> = getPaginated("$baseUrl/houses") {
        parameter("name", name)
        parameter("page", page)
        parameter("pageSize", pageSize)
    }

    suspend fun getHousesByQuery(
        name: String? = null,
        region: String? = null,
        words: String? = null,
        hasWords: Boolean? = null,
        hasTitles: Boolean? = null,
        hasSeats: Boolean? = null,
        hasDiedOut: Boolean? = null,
        hasAncestralWeapons: Boolean? = null,
        page: Int = 1,
        pageSize: Int = 10,
    ): PaginatedResult<House> = getPaginated("$baseUrl/houses") {
        name?.let { parameter("name", it) }
        region?.let { parameter("region", it) }
        words?.let { parameter("words", it) }
        hasWords?.let { parameter("hasWords", it) }
        hasTitles?.let { parameter("hasTitles", it) }
        hasSeats?.let { parameter("hasSeats", it) }
        hasDiedOut?.let { parameter("hasDiedOut", it) }
        hasAncestralWeapons?.let { parameter("hasAncestralWeapons", it) }
        parameter("page", page)
        parameter("pageSize", pageSize)
    }

    suspend fun getHousesByUrl(
        url: String,
    ): PaginatedResult<House> = getPaginated(url)

    // ── Books ───────────────────────────────────────────────────

    suspend fun getBooks(
        page: Int = 1,
        pageSize: Int = 10,
    ): PaginatedResult<Book> = getPaginated("$baseUrl/books") {
        parameter("page", page)
        parameter("pageSize", pageSize)
    }

    suspend fun getBooksByName(
        name: String,
        page: Int = 1,
        pageSize: Int = 10,
    ): PaginatedResult<Book> = getPaginated("$baseUrl/books") {
        parameter("name", name)
        parameter("page", page)
        parameter("pageSize", pageSize)
    }

    suspend fun getBooksByUrl(
        url: String,
    ): PaginatedResult<Book> = getPaginated(url)

    // ── Internal ────────────────────────────────────────────────

    /**
     * Performs a GET request and parses the response into a [PaginatedResult].
     */
    private suspend inline fun <reified T> getPaginated(
        url: String,
        crossinline block: io.ktor.client.request.HttpRequestBuilder.() -> Unit = {},
    ): PaginatedResult<T> {
        val response: HttpResponse = httpClient.get(url) { block() }
        val data: List<T> = response.body()
        return PaginationParser.parse(data, response.headers)
    }
}

