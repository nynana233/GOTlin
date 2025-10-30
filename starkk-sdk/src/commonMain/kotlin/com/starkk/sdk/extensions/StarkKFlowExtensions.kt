package com.starkk.sdk.extensions

import com.starkk.sdk.StarkKClient
import com.starkk.sdk.StarkKException
import com.starkk.sdk.models.Book
import com.starkk.sdk.models.Character
import com.starkk.sdk.models.House
import com.starkk.sdk.models.StarkKPage
import com.starkk.sdk.models.StarkKPageResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Coroutine [Flow] extensions for [StarkKClient] that automatically
 * walk through every page of a paginated endpoint.
 *
 * Each emission is the list of items from a single page. The flow
 * completes when there is no `next` page URL, or when an error occurs
 * (the error is thrown into the flow collector).
 */

// Characters

/**
 * Returns a [Flow] that emits each page of characters sequentially
 * until all pages have been exhausted.
 *
 * @param page Starting page (default `1`).
 * @param pageSize Items per page (default `10`, max `50`).
 */
fun StarkKClient.getCharactersAsFlow(
    page: Int = 1,
    pageSize: Int = 10,
): Flow<List<Character>> = paginatedFlow(
    firstPage = { getCharacters(page, pageSize) },
    nextPage = { url -> getCharactersByUrl(url) },
)

/**
 * Returns a [Flow] that emits each page of characters matching
 * the given query parameters.
 */
fun StarkKClient.getCharactersAsFlow(
    name: String? = null,
    gender: String? = null,
    culture: String? = null,
    born: String? = null,
    died: String? = null,
    isAlive: Boolean? = null,
    page: Int = 1,
    pageSize: Int = 10,
): Flow<List<Character>> = paginatedFlow(
    firstPage = { getCharacters(name, gender, culture, born, died, isAlive, page, pageSize) },
    nextPage = { url -> getCharactersByUrl(url) },
)

// Houses

/**
 * Returns a [Flow] that emits each page of houses sequentially.
 */
fun StarkKClient.getHousesAsFlow(
    page: Int = 1,
    pageSize: Int = 10,
): Flow<List<House>> = paginatedFlow(
    firstPage = { getHouses(page, pageSize) },
    nextPage = { url -> getHousesByUrl(url) },
)

/**
 * Returns a [Flow] that emits each page of houses matching
 * the given query parameters.
 */
fun StarkKClient.getHousesAsFlow(
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
): Flow<List<House>> = paginatedFlow(
    firstPage = {
        getHouses(
            name, region, words, hasWords, hasTitles,
            hasSeats, hasDiedOut, hasAncestralWeapons, page, pageSize,
        )
    },
    nextPage = { url -> getHousesByUrl(url) },
)

// Books

/**
 * Returns a [Flow] that emits each page of books sequentially.
 */
fun StarkKClient.getBooksAsFlow(
    page: Int = 1,
    pageSize: Int = 10,
): Flow<List<Book>> = paginatedFlow(
    firstPage = { getBooks(page, pageSize) },
    nextPage = { url -> getBooksByUrl(url) },
)

// Internal helper

/**
 * Generic pagination helper.
 *
 * Calls [firstPage] to get the initial result, emits its data,
 * then follows the `next` URL repeatedly until exhausted.
 */
private fun <T> paginatedFlow(
    firstPage: suspend () -> StarkKPageResult<T>,
    nextPage: suspend (url: String) -> StarkKPageResult<T>,
): Flow<List<T>> = flow {
    var page: StarkKPage<T> = firstPage().unwrap()
    emit(page.items)

    while (page.hasNext) {
        page = nextPage(page.nextUrl!!).unwrap()
        emit(page.items)
    }
}

/**
 * Unwraps a [StarkKPageResult] into a [StarkKPage], throwing
 * the [StarkKException] if the result is a failure.
 */
private fun <T> StarkKPageResult<T>.unwrap(): StarkKPage<T> = when (this) {
    is StarkKPageResult.Success -> page
    is StarkKPageResult.Failure -> throw exception
}

