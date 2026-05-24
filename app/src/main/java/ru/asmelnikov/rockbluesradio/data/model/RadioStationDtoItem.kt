package ru.asmelnikov.rockbluesradio.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class RadioStationDtoItem(
    @SerialName("bitrate")
    @Ignore
    val bitrate: Int? = null,
    @SerialName("changeuuid")
    @Ignore
    val changeUuid: String? = null,
    @SerialName("clickcount")
    @Ignore
    val clickCount: Int? = null,
    @SerialName("clicktimestamp")
    @Ignore
    val clickTimestamp: String? = null,
    @SerialName("clicktimestamp_iso8601")
    @Ignore
    val clickTimestampIso8601: String? = null,
    @SerialName("clicktrend")
    @Ignore
    val clickTrend: Int? = null,
    @SerialName("codec")
    @Ignore
    val codec: String? = null,
    @SerialName("country")
    @Ignore
    val country: String? = null,
    @SerialName("countrycode")
    @Ignore
    val countryCode: String? = null,
    @SerialName("favicon")
    @ColumnInfo("favicon")
    val favicon: String? = null,
    @SerialName("geo_lat")
    @Ignore
    val geoLat: Double? = null,
    @SerialName("geo_long")
    @Ignore
    val geoLong: Double? = null,
    @SerialName("geo_distance")
    @Ignore
    val geoDistance: Double? = null,
    @SerialName("has_extended_info")
    @Ignore
    val hasExtendedInfo: Boolean? = null,
    @SerialName("hls")
    @Ignore
    val hls: Int? = null,
    @SerialName("homepage")
    @Ignore
    val homepage: String? = null,
    @SerialName("iso_3166_2")
    @Ignore
    val iso31662: String? = null,
    @SerialName("language")
    @Ignore
    val language: String? = null,
    @SerialName("languagecodes")
    @Ignore
    val languageCodes: String? = null,
    @SerialName("lastchangetime")
    @Ignore
    val lastChangeTime: String? = null,
    @SerialName("lastchangetime_iso8601")
    @Ignore
    val lastChangeTimeIso8601: String? = null,
    @SerialName("lastcheckok")
    @Ignore
    val lastCheckOk: Int? = null,
    @SerialName("lastcheckoktime")
    @Ignore
    val lastCheckOkTime: String? = null,
    @SerialName("lastcheckoktime_iso8601")
    @Ignore
    val lastCheckOkTimeIso8601: String? = null,
    @SerialName("lastchecktime")
    @Ignore
    val lastCheckTime: String? = null,
    @SerialName("lastchecktime_iso8601")
    @Ignore
    val lastCheckTimeIso8601: String? = null,
    @SerialName("lastlocalchecktime")
    @Ignore
    val lastLocalCheckTime: String? = null,
    @SerialName("lastlocalchecktime_iso8601")
    @Ignore
    val lastLocalCheckTimeIso8601: String? = null,
    @SerialName("name")
    @ColumnInfo("name")
    val name: String? = null,
    @SerialName("serveruuid")
    @Ignore
    val serverUuid: String? = null,
    @SerialName("ssl_error")
    @Ignore
    val sslError: Int? = null,
    @SerialName("state")
    @Ignore
    val state: String? = null,
    @SerialName("stationuuid")
    @ColumnInfo("stationuuid")
    @PrimaryKey(autoGenerate = false)
    val stationUuid: String = "",
    @SerialName("tags")
    @ColumnInfo("tags")
    val tags: String? = null,
    @SerialName("url")
    @Ignore
    val url: String? = null,
    @SerialName("url_resolved")
    @ColumnInfo("url_resolved")
    val urlResolved: String? = null,
    @SerialName("votes")
    @Ignore
    val votes: Int? = null
)