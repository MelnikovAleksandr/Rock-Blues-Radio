package ru.asmelnikov.rockbluesradio.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.asmelnikov.rockbluesradio.domain.model.Genre
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation

interface RadioStationRepository {

    suspend fun getStationsByGenre(genre: Genre): List<RadioStation>

    fun observeFavoriteStations(): Flow<List<RadioStation>>

    suspend fun isFavorite(stationId: String): Boolean

    suspend fun addToFavorites(station: RadioStation)

    suspend fun removeFromFavorites(station: RadioStation)
}
