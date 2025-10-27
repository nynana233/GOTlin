package com.starkk.sdk

import com.starkk.sdk.models.Book
import com.starkk.sdk.models.Character
import com.starkk.sdk.models.House
import com.starkk.sdk.models.StarkKPage
import com.starkk.sdk.models.StarkKPageResult
import com.starkk.sdk.network.IceAndFireApi
import com.starkk.sdk.network.createPlatformHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.io.IOException
import kotlinx.serialization.json.Json

/**
 * Main entry-point for the StarkK SDK — a Kotlin Multiplatform SDK
 * for [An API of Ice And Fire](https://anapioficeandfire.com/).
 *
 * Obtain an instance via [Builder]:
 *
 * ```kotlin
 * val client = StarkKClient.Builder()
 *     .baseUrl("https://anapioficeandfire.com/api")
 *     .build()
 * ```
 *
 * All public functions are **suspend** functions that return
 * [StarkKPageResult]<T> so callers can handle success/failure
 * idiomatically with `.onSuccess {}` / `.onFailure {}`.
 */
class StarkKClient internal constructor(
    private val api: IceAndFireApi,
    private val httpClient: HttpClient,
) {

    // ── Characters ──────────────────────────────────────────────

    /**
     * Fetch a page of characters.
     *
     * @param page 1-based page index (default `1`).
     * @param pageSize Number of results per page (default `10`, max `50`).
     */
    suspend fun getCharacters(
        page: Int = 1,
        pageSize: Int = 10,
    ): StarkKPageResult<Character> = safeApiCall(page) {
        api.getCharacters(page, pageSize)
    }

    /**
     * Search characters by exact name.
     */
    suspend fun getCharactersByName(
        name: String,
        page: Int = 1,
        pageSize: Int = 10,
    ): StarkKPageResult<Character> = safeApiCall(page) {
        api.getCharactersByName(name, page, pageSize)
    }

    /**
     * Search characters with flexible query parameters.
     */
    suspend fun getCharacters(
        name: String? = null,
        gender: String? = null,
        culture: String? = null,
        born: String? = null,
        died: String? = null,
        isAlive: Boolean? = null,
        page: Int = 1,
        pageSize: Int = 10,
    ): StarkKPageResult<Character> = safeApiCall(page) {
        api.getCharactersByQuery(name, gender, culture, born, died, isAlive, page, pageSize)
    }

    /**
     * Fetch the next page of characters from the given [page] cursor.
     *
     * @return The next [StarkKPageResult], or `null` if there is no next page.
     */
    suspend fun nextCharacters(
        page: StarkKPage<Character>,
    ): StarkKPageResult<Character>? {
        val url = page.nextUrl ?: return null
        return safeApiCall { api.getCharactersByUrl(url) }
    }

    /**
     * Fetch the previous page of characters from the given [page] cursor.
     *
     * @return The previous [StarkKPageResult], or `null` if there is no previous page.
     */
    suspend fun previousCharacters(
        page: StarkKPage<Character>,
    ): StarkKPageResult<Character>? {
        val url = page.prevUrl ?: return null
        return safeApiCall { api.getCharactersByUrl(url) }
    }

    /**
     * Fetch a page of characters using a full URL (typically from pagination).
     * @suppress Internal — use [nextCharacters] / [previousCharacters] instead.
     */
    internal suspend fun getCharactersByUrl(
        url: String,
    ): StarkKPageResult<Character> = safeApiCall {
        api.getCharactersByUrl(url)
    }

    // ── Houses ──────────────────────────────────────────────────

    /**
     * Fetch a page of houses.
     */
    suspend fun getHouses(
        page: Int = 1,
        pageSize: Int = 10,
    ): StarkKPageResult<House> = safeApiCall(page) {
        api.getHouses(page, pageSize)
    }

    /**
     * Search houses by exact name.
     */
    suspend fun getHousesByName(
        name: String,
        page: Int = 1,
        pageSize: Int = 10,
    ): StarkKPageResult<House> = safeApiCall(page) {
        api.getHousesByName(name, page, pageSize)
    }

    /**
     * Search houses with flexible query parameters.
     */
    suspend fun getHouses(
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
    ): StarkKPageResult<House> = safeApiCall(page) {
        api.getHousesByQuery(
            name, region, words, hasWords, hasTitles,
            hasSeats, hasDiedOut, hasAncestralWeapons, page, pageSize,
        )
    }

    /**
     * Fetch the next page of houses from the given [page] cursor.
     *
     * @return The next [StarkKPageResult], or `null` if there is no next page.
     */
    suspend fun nextHouses(
        page: StarkKPage<House>,
    ): StarkKPageResult<House>? {
        val url = page.nextUrl ?: return null
        return safeApiCall { api.getHousesByUrl(url) }
    }

    /**
     * Fetch the previous page of houses from the given [page] cursor.
     *
     * @return The previous [StarkKPageResult], or `null` if there is no previous page.
     */
    suspend fun previousHouses(
        page: StarkKPage<House>,
    ): StarkKPageResult<House>? {
        val url = page.prevUrl ?: return null
        return safeApiCall { api.getHousesByUrl(url) }
    }

    /**
     * Fetch a page of houses using a full URL (typically from pagination).
     * @suppress Internal — use [nextHouses] / [previousHouses] instead.
     */
    internal suspend fun getHousesByUrl(
        url: String,
    ): StarkKPageResult<House> = safeApiCall {
        api.getHousesByUrl(url)
    }

    // ── Books ───────────────────────────────────────────────────

    /**
     * Fetch a page of books.
     */
    suspend fun getBooks(
        page: Int = 1,
        pageSize: Int = 10,
    ): StarkKPageResult<Book> = safeApiCall(page) {
        api.getBooks(page, pageSize)
    }

    /**
     * Search books by exact name.
     */
    suspend fun getBooksByName(
        name: String,
        page: Int = 1,
        pageSize: Int = 10,
    ): StarkKPageResult<Book> = safeApiCall(page) {
        api.getBooksByName(name, page, pageSize)
    }

    /**
     * Fetch the next page of books from the given [page] cursor.
     *
     * @return The next [StarkKPageResult], or `null` if there is no next page.
     */
    suspend fun nextBooks(
        page: StarkKPage<Book>,
    ): StarkKPageResult<Book>? {
        val url = page.nextUrl ?: return null
        return safeApiCall { api.getBooksByUrl(url) }
    }

    /**
     * Fetch the previous page of books from the given [page] cursor.
     *
     * @return The previous [StarkKPageResult], or `null` if there is no previous page.
     */
    suspend fun previousBooks(
        page: StarkKPage<Book>,
    ): StarkKPageResult<Book>? {
        val url = page.prevUrl ?: return null
        return safeApiCall { api.getBooksByUrl(url) }
    }

    /**
     * Fetch a page of books using a full URL (typically from pagination).
     * @suppress Internal — use [nextBooks] / [previousBooks] instead.
     */
    internal suspend fun getBooksByUrl(
        url: String,
    ): StarkKPageResult<Book> = safeApiCall {
        api.getBooksByUrl(url)
    }

    // ── Lifecycle ───────────────────────────────────────────────

    /**
     * Closes the underlying HTTP client and releases resources.
     */
    fun close() {
        httpClient.close()
    }

    // ── Internals ───────────────────────────────────────────────

    /**
     * Wraps an API call in [StarkKPageResult], mapping exceptions
     * to the appropriate [StarkKException] subtype.
     *
     * @param requestedPage The page number the caller requested (used as
     *                      fallback when the Link header cannot be parsed).
     */
    private suspend inline fun <T> safeApiCall(
        requestedPage: Int = 1,
        crossinline call: suspend () -> com.starkk.sdk.models.PaginatedResult<T>,
    ): StarkKPageResult<T> = try {
        val paginated = call()
        StarkKPageResult.Success(StarkKPage.from(paginated, requestedPage))
    } catch (e: io.ktor.client.plugins.ClientRequestException) {
        StarkKPageResult.Failure(
            StarkKException.HttpError(e.response.status.value, e.response.status.description),
        )
    } catch (e: io.ktor.client.plugins.ServerResponseException) {
        StarkKPageResult.Failure(
            StarkKException.HttpError(e.response.status.value, e.response.status.description),
        )
    } catch (e: io.ktor.client.plugins.ResponseException) {
        StarkKPageResult.Failure(
            StarkKException.HttpError(e.response.status.value, e.response.status.description),
        )
    } catch (e: IOException) {
        StarkKPageResult.Failure(StarkKException.NetworkError(e))
    } catch (e: Exception) {
        StarkKPageResult.Failure(StarkKException.UnknownError(e))
    }

    // ── Builder ─────────────────────────────────────────────────

    /**
     * Builder for [StarkKClient].
     *
     * ```kotlin
     * val client = StarkKClient.Builder()
     *     .baseUrl("https://anapioficeandfire.com/api")
     *     .enableLogging(true)
     *     .build()
     * ```
     */
    class Builder {
        private var baseUrl: String = BASE_URL
        private var httpClient: HttpClient? = null
        private var loggingEnabled: Boolean = false
        private var connectTimeoutMillis: Long = 30_000
        private var requestTimeoutMillis: Long = 30_000

        /**
         * Override the default base URL.
         * Useful for testing against a mock server.
         */
        fun baseUrl(url: String) = apply { this.baseUrl = url }

        /**
         * Supply a fully-configured [HttpClient].
         * When set, [enableLogging] and timeout settings are ignored.
         */
        fun httpClient(client: HttpClient) = apply { this.httpClient = client }

        /** Enable/disable HTTP request & response logging. */
        fun enableLogging(enabled: Boolean) = apply { this.loggingEnabled = enabled }

        /** Set the connect timeout in seconds (default `30`). */
        fun connectTimeout(seconds: Long) = apply { this.connectTimeoutMillis = seconds * 1000 }

        /** Set the request timeout in seconds (default `30`). */
        fun requestTimeout(seconds: Long) = apply { this.requestTimeoutMillis = seconds * 1000 }

        /**
         * Build the [StarkKClient] instance.
         */
        fun build(): StarkKClient {
            val client = httpClient ?: buildDefaultHttpClient()
            val api = IceAndFireApi(client, baseUrl)
            return StarkKClient(api, client)
        }

        private fun buildDefaultHttpClient(): HttpClient {
            return createPlatformHttpClient {
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                        coerceInputValues = true
                        isLenient = true
                    })
                }

                install(io.ktor.client.plugins.HttpTimeout) {
                    connectTimeoutMillis = this@Builder.connectTimeoutMillis
                    requestTimeoutMillis = this@Builder.requestTimeoutMillis
                }

                if (loggingEnabled) {
                    install(Logging) {
                        level = LogLevel.BODY
                    }
                }
            }
        }
    }

    companion object {
        internal const val BASE_URL = "https://anapioficeandfire.com/api"
    }
}

