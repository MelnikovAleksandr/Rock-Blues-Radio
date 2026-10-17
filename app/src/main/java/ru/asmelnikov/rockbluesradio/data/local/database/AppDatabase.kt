package ru.asmelnikov.rockbluesradio.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FavoriteStationEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteStationDao(): FavoriteStationDao

    companion object {
        const val NAME = "radio_stations.db"
    }
}
