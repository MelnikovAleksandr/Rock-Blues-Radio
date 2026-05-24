package ru.asmelnikov.rockbluesradio.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.asmelnikov.rockbluesradio.data.model.RadioStationDtoItem

@Dao
interface FavoriteRadioStationsDao {
    @Query("SELECT * FROM RadioStationDtoItem ORDER BY name LIMIT :limit OFFSET :offset")
    fun getAllFavoriteRadioStations(limit: Int, offset: Int): Flow<List<RadioStationDtoItem>>

    @Query("SELECT stationuuid FROM RadioStationDtoItem WHERE stationuuid = :stationUuid")
    suspend fun getFavoriteRadioStationByStationUUID(stationUuid: String): RadioStationDtoItem?

    @Insert
    suspend fun insertRadioStations(vararg radioStations: RadioStationDtoItem)

    @Delete
    suspend fun deleteRadioStations(vararg radioStations: RadioStationDtoItem)
}