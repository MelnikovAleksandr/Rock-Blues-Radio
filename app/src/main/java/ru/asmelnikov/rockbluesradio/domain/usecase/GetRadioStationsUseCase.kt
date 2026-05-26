package ru.asmelnikov.rockbluesradio.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.asmelnikov.rockbluesradio.domain.model.Genre
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation
import ru.asmelnikov.rockbluesradio.domain.repository.RadioStationRepository

class GetRadioStationsUseCase(
    private val repository: RadioStationRepository
) {
    fun execute(genre: Genre): Flow<List<RadioStation>> {
        return repository.getFavoriteRadioStations()
            .map { favoriteStations ->
                val radioStationsFromApi = repository.getRadioStationsByGenre(genre)
                radioStationsFromApi.map { radioStation ->
                    radioStation.copy(
                        isFavorite = favoriteStations.any { it.id == radioStation.id }
                    )
                }
            }
    }
}