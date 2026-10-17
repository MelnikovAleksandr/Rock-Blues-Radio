package ru.asmelnikov.rockbluesradio.data.di

import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import ru.asmelnikov.rockbluesradio.data.connectivity.AndroidConnectivityObserver
import ru.asmelnikov.rockbluesradio.data.local.asset.AssetStationsDataSource
import ru.asmelnikov.rockbluesradio.data.local.asset.StationsDataSource
import ru.asmelnikov.rockbluesradio.data.local.database.AppDatabase
import ru.asmelnikov.rockbluesradio.data.repository.RadioStationRepositoryImpl
import ru.asmelnikov.rockbluesradio.domain.connectivity.ConnectivityObserver
import ru.asmelnikov.rockbluesradio.domain.repository.RadioStationRepository

val dataModule = module {
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, AppDatabase.NAME).build()
    }
    single { get<AppDatabase>().favoriteStationDao() }
    singleOf(::AssetStationsDataSource) { bind<StationsDataSource>() }
    singleOf(::RadioStationRepositoryImpl) { bind<RadioStationRepository>() }
    singleOf(::AndroidConnectivityObserver) { bind<ConnectivityObserver>() }
}
