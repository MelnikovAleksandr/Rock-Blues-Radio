package ru.asmelnikov.rockbluesradio.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteStationDao {

    @Query("SELECT * FROM RadioStationDtoItem ORDER BY rowid DESC")
    fun observeAll(): Flow<List<FavoriteStationEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM RadioStationDtoItem WHERE stationuuid = :stationId)")
    suspend fun exists(stationId: String): Boolean

    @Insert
    suspend fun insert(station: FavoriteStationEntity)

    @Delete
    suspend fun delete(station: FavoriteStationEntity)
}
