package ru.asmelnikov.rockbluesradio.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.asmelnikov.rockbluesradio.data.model.RadioStationDtoItem

@Database(entities = [RadioStationDtoItem::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract val radioStationsDao: FavoriteRadioStationsDao
}