package ru.asmelnikov.rockbluesradio.data.local.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = FavoriteStationEntity.TABLE_NAME)
data class FavoriteStationEntity(
    @ColumnInfo(name = "favicon") val favicon: String?,
    @ColumnInfo(name = "name") val name: String?,
    @PrimaryKey @ColumnInfo(name = "stationuuid") val stationUuid: String,
    @ColumnInfo(name = "tags") val tags: String?,
    @ColumnInfo(name = "url_resolved") val urlResolved: String?,
) {
    companion object {
        const val TABLE_NAME = "RadioStationDtoItem"
    }
}
