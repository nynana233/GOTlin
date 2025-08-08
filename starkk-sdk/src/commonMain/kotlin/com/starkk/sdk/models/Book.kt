package com.starkk.sdk.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a book from "An API of Ice And Fire".
 */
@Serializable
data class Book(
    @SerialName("url") val url: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("isbn") val isbn: String = "",
    @SerialName("authors") val authors: List<String> = emptyList(),
    @SerialName("numberOfPages") val numberOfPages: Int = 0,
    @SerialName("publisher") val publisher: String = "",
    @SerialName("country") val country: String = "",
    @SerialName("mediaType") val mediaType: String = "",
    @SerialName("released") val released: String = "",
    @SerialName("characters") val characters: List<String> = emptyList(),
    @SerialName("povCharacters") val povCharacters: List<String> = emptyList(),
)

