package ru.asmelnikov.rockbluesradio.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.asmelnikov.rockbluesradio.data.local.asset.StationsAssetDataSource
import ru.asmelnikov.rockbluesradio.data.local.database.FavoriteStationDao
import ru.asmelnikov.rockbluesradio.data.mapper.toDomain
import ru.asmelnikov.rockbluesradio.data.mapper.toFavoriteEntity
import ru.asmelnikov.rockbluesradio.domain.model.Genre
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation
import ru.asmelnikov.rockbluesradio.domain.repository.RadioStationRepository

class RadioStationRepositoryImpl(
    private val assetDataSource: StationsAssetDataSource,
    private val favoriteStationDao: FavoriteStationDao,
) : RadioStationRepository {

    override suspend fun getStationsByGenre(genre: Genre): List<RadioStation> =
        assetDataSource.getStations(genre).map { it.toDomain() }

    override fun observeFavoriteStations(): Flow<List<RadioStation>> =
        favoriteStationDao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun isFavorite(stationId: String): Boolean = favoriteStationDao.exists(stationId)

    override suspend fun addToFavorites(station: RadioStation) {
        favoriteStationDao.insert(station.toFavoriteEntity())
    }

    override suspend fun removeFromFavorites(station: RadioStation) {
        favoriteStationDao.delete(station.toFavoriteEntity())
    }
}
