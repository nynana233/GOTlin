package com.starkk.sdk.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a noble house from "An API of Ice And Fire".
 */
@Serializable
data class House(
    @SerialName("url") val url: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("region") val region: String = "",
    @SerialName("coatOfArms") val coatOfArms: String = "",
    @SerialName("words") val words: String = "",
    @SerialName("titles") val titles: List<String> = emptyList(),
    @SerialName("seats") val seats: List<String> = emptyList(),
    @SerialName("currentLord") val currentLord: String = "",
    @SerialName("heir") val heir: String = "",
    @SerialName("overlord") val overlord: String = "",
    @SerialName("founded") val founded: String = "",
    @SerialName("founder") val founder: String = "",
    @SerialName("diedOut") val diedOut: String = "",
    @SerialName("ancestralWeapons") val ancestralWeapons: List<String> = emptyList(),
    @SerialName("cadetBranches") val cadetBranches: List<String> = emptyList(),
    @SerialName("swornMembers") val swornMembers: List<String> = emptyList(),
)

