package ru.asmelnikov.rockbluesradio.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
abstract class FavoriteStationDao {

    @Query("SELECT * FROM RadioStationDtoItem ORDER BY rowid DESC")
    abstract fun observeAll(): Flow<List<FavoriteStationEntity>>

    @Transaction
    open suspend fun toggle(station: FavoriteStationEntity) {
        if (exists(station.stationUuid)) {
            delete(station.stationUuid)
        } else {
            insert(station)
        }
    }

    @Query("SELECT EXISTS(SELECT 1 FROM RadioStationDtoItem WHERE stationuuid = :stationId)")
    abstract suspend fun exists(stationId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(station: FavoriteStationEntity)

    @Query("DELETE FROM RadioStationDtoItem WHERE stationuuid = :stationId")
    abstract suspend fun delete(stationId: String)
}
