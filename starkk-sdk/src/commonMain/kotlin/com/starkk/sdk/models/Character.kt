package com.starkk.sdk.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a character from "An API of Ice And Fire".
 *
 * All fields use safe defaults so that missing JSON fields
 * never cause deserialization failures.
 */
@Serializable
data class Character(
    @SerialName("url") val url: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("gender") val gender: String = "",
    @SerialName("culture") val culture: String = "",
    @SerialName("born") val born: String = "",
    @SerialName("died") val died: String = "",
    @SerialName("titles") val titles: List<String> = emptyList(),
    @SerialName("aliases") val aliases: List<String> = emptyList(),
    @SerialName("father") val father: String = "",
    @SerialName("mother") val mother: String = "",
    @SerialName("spouse") val spouse: String = "",
    @SerialName("allegiances") val allegiances: List<String> = emptyList(),
    @SerialName("books") val books: List<String> = emptyList(),
    @SerialName("povBooks") val povBooks: List<String> = emptyList(),
    @SerialName("tvSeries") val tvSeries: List<String> = emptyList(),
    @SerialName("playedBy") val playedBy: List<String> = emptyList(),
)

