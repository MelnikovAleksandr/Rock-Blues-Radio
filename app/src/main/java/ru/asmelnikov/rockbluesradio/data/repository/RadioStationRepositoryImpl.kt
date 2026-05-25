package ru.asmelnikov.rockbluesradio.data.repository

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import ru.asmelnikov.rockbluesradio.data.database.FavoriteRadioStationsDao
import ru.asmelnikov.rockbluesradio.data.model.RadioStationDtoItem
import ru.asmelnikov.rockbluesradio.data.model.map
import ru.asmelnikov.rockbluesradio.data.model.mapToDto
import ru.asmelnikov.rockbluesradio.domain.model.Genre
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation
import ru.asmelnikov.rockbluesradio.domain.repository.RadioStationRepository

class RadioStationRepositoryImpl(
    private val radioStationsDao: FavoriteRadioStationsDao,
    private val context: Context
) : RadioStationRepository {

    companion object {
        private val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }

    override suspend fun getRadioStationsByGenre(genre: Genre): List<RadioStation> {
        return withContext(Dispatchers.IO) {
            val fileName = when (genre) {
                Genre.Blues -> "blues_data.json"
                Genre.Rock -> "rock_data.json"
            }

            val jsonString = context.assets.open(fileName).bufferedReader().use {
                it.readText()
            }
            val dtoList = json.decodeFromString<List<RadioStationDtoItem>>(jsonString)
            dtoList.map { it.map(false) }
        }
    }

    override fun getFavoriteRadioStations(): Flow<List<RadioStation>> {
        return radioStationsDao.getAllFavoriteRadioStations().map {
            it.map { item -> item.map(isFavorite = true) }
        }
    }

    override suspend fun getFavoriteRadioStationByStationUUID(stationUuid: String): RadioStation? {
        return radioStationsDao.getFavoriteRadioStationByStationUUID(stationUuid)
            ?.map(isFavorite = true)
    }

    override suspend fun addToFavorites(station: RadioStation) {
        radioStationsDao.insertRadioStations(station.mapToDto())
    }

    override suspend fun removeFromFavorites(station: RadioStation) {
        radioStationsDao.deleteRadioStations(station.mapToDto())
    }
}