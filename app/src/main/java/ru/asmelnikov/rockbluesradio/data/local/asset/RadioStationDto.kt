package ru.asmelnikov.rockbluesradio.data.local.asset

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RadioStationDto(
    @SerialName("stationuuid") val stationUuid: String,
    @SerialName("name") val name: String? = null,
    @SerialName("url_resolved") val urlResolved: String? = null,
    @SerialName("favicon") val favicon: String? = null,
    @SerialName("tags") val tags: String? = null,
)
