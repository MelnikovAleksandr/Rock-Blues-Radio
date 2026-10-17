package ru.asmelnikov.rockbluesradio.testing

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import ru.asmelnikov.rockbluesradio.domain.model.Genre
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation
import ru.asmelnikov.rockbluesradio.domain.repository.RadioStationRepository

class FakeRadioStationRepository(
    private val stationsByGenre: Map<Genre, List<RadioStation>> = emptyMap(),
    favorites: List<RadioStation> = emptyList(),
) : RadioStationRepository {

    val favorites = MutableStateFlow(favorites.map { it.copy(isFavorite = true) })
    val requestedGenres = mutableListOf<Genre>()
    val toggledStations = mutableListOf<RadioStation>()
    var stationsError: Throwable? = null

    override suspend fun getStationsByGenre(genre: Genre): List<RadioStation> {
        requestedGenres += genre
        stationsError?.let { throw it }
        return stationsByGenre[genre].orEmpty()
    }

    override fun observeFavoriteStations(): Flow<List<RadioStation>> = favorites

    override suspend fun toggleFavorite(station: RadioStation) {
        toggledStations += station
        favorites.update { current ->
            if (current.any { it.id == station.id }) {
                current.filterNot { it.id == station.id }
            } else {
                listOf(station.copy(isFavorite = true)) + current
            }
        }
    }
}
