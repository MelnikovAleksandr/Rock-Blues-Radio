package ru.asmelnikov.rockbluesradio.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.asmelnikov.rockbluesradio.domain.model.Genre
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation

interface RadioStationRepository {

    suspend fun getRadioStationsByGenre(
        genre: Genre
    ): List<RadioStation>

    fun getFavoriteRadioStations(): Flow<List<RadioStation>>

    suspend fun getFavoriteRadioStationByStationUUID(stationUuid: String): RadioStation?

    suspend fun addToFavorites(station: RadioStation)

    suspend fun removeFromFavorites(station: RadioStation)

}