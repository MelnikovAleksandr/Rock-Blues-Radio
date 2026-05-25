package ru.asmelnikov.rockbluesradio.data.di

import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ru.asmelnikov.rockbluesradio.data.database.AppDatabase
import ru.asmelnikov.rockbluesradio.data.database.FavoriteRadioStationsDao
import ru.asmelnikov.rockbluesradio.data.repository.RadioStationRepositoryImpl
import ru.asmelnikov.rockbluesradio.domain.repository.RadioStationRepository

val dataModule = module {

    single<AppDatabase> {
        Room.databaseBuilder(
            context = androidContext().applicationContext,
            klass = AppDatabase::class.java,
            name = "radio_stations.db"
        ).build()
    }

    single<FavoriteRadioStationsDao> {
        get<AppDatabase>().radioStationsDao
    }

    single<RadioStationRepository> {
        RadioStationRepositoryImpl(
            radioStationsDao = get(),
            context = get()
        )
    }
}