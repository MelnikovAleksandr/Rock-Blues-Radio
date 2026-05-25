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
    var bitrate: Int? = null,
    @SerialName("changeuuid")
    @Ignore
    var changeUuid: String? = null,
    @SerialName("clickcount")
    @Ignore
    var clickCount: Int? = null,
    @SerialName("clicktimestamp")
    @Ignore
    var clickTimestamp: String? = null,
    @SerialName("clicktimestamp_iso8601")
    @Ignore
    var clickTimestampIso8601: String? = null,
    @SerialName("clicktrend")
    @Ignore
    var clickTrend: Int? = null,
    @SerialName("codec")
    @Ignore
    var codec: String? = null,
    @SerialName("country")
    @Ignore
    var country: String? = null,
    @SerialName("countrycode")
    @Ignore
    var countryCode: String? = null,
    @SerialName("favicon")
    @ColumnInfo("favicon")
    var favicon: String? = null,
    @SerialName("geo_lat")
    @Ignore
    var geoLat: Double? = null,
    @SerialName("geo_long")
    @Ignore
    var geoLong: Double? = null,
    @SerialName("geo_distance")
    @Ignore
    var geoDistance: Double? = null,
    @SerialName("has_extended_info")
    @Ignore
    var hasExtendedInfo: Boolean? = null,
    @SerialName("hls")
    @Ignore
    var hls: Int? = null,
    @SerialName("homepage")
    @Ignore
    var homepage: String? = null,
    @SerialName("iso_3166_2")
    @Ignore
    var iso31662: String? = null,
    @SerialName("language")
    @Ignore
    var language: String? = null,
    @SerialName("languagecodes")
    @Ignore
    var languageCodes: String? = null,
    @SerialName("lastchangetime")
    @Ignore
    var lastChangeTime: String? = null,
    @SerialName("lastchangetime_iso8601")
    @Ignore
    var lastChangeTimeIso8601: String? = null,
    @SerialName("lastcheckok")
    @Ignore
    var lastCheckOk: Int? = null,
    @SerialName("lastcheckoktime")
    @Ignore
    var lastCheckOkTime: String? = null,
    @SerialName("lastcheckoktime_iso8601")
    @Ignore
    var lastCheckOkTimeIso8601: String? = null,
    @SerialName("lastchecktime")
    @Ignore
    var lastCheckTime: String? = null,
    @SerialName("lastchecktime_iso8601")
    @Ignore
    var lastCheckTimeIso8601: String? = null,
    @SerialName("lastlocalchecktime")
    @Ignore
    var lastLocalCheckTime: String? = null,
    @SerialName("lastlocalchecktime_iso8601")
    @Ignore
    var lastLocalCheckTimeIso8601: String? = null,
    @SerialName("name")
    @ColumnInfo("name")
    var name: String? = null,
    @SerialName("serveruuid")
    @Ignore
    var serverUuid: String? = null,
    @SerialName("ssl_error")
    @Ignore
    var sslError: Int? = null,
    @SerialName("state")
    @Ignore
    var state: String? = null,
    @SerialName("stationuuid")
    @ColumnInfo("stationuuid")
    @PrimaryKey(autoGenerate = false)
    var stationUuid: String = "",
    @SerialName("tags")
    @ColumnInfo("tags")
    var tags: String? = null,
    @SerialName("url")
    @Ignore
    var url: String? = null,
    @SerialName("url_resolved")
    @ColumnInfo("url_resolved")
    var urlResolved: String? = null,
    @SerialName("votes")
    @Ignore
    var votes: Int? = null
)