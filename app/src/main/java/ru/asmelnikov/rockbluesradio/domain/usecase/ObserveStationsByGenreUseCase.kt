package ru.asmelnikov.rockbluesradio.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.asmelnikov.rockbluesradio.domain.model.Genre
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation
import ru.asmelnikov.rockbluesradio.domain.repository.RadioStationRepository

class ObserveStationsByGenreUseCase(private val repository: RadioStationRepository) {

    operator fun invoke(genre: Genre): Flow<List<RadioStation>> = repository.observeFavoriteStations()
        .map { favorites ->
            val favoriteIds = favorites.mapTo(HashSet()) { it.id }
            repository.getStationsByGenre(genre).map { station ->
                station.copy(isFavorite = station.id in favoriteIds)
            }
        }
}
