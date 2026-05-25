package ru.asmelnikov.rockbluesradio.domain.usecase

import kotlinx.coroutines.flow.firstOrNull
import ru.asmelnikov.rockbluesradio.domain.model.Genre
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation
import ru.asmelnikov.rockbluesradio.domain.repository.RadioStationRepository

class GetRadioStationsUseCase(
    private val repository: RadioStationRepository
) {
    suspend fun execute(
        genre: Genre
    ): Result<List<RadioStation>> {
        val radioStationsFromApi = repository.getRadioStationsByGenre(genre)
        val favoriteStations = repository.getFavoriteRadioStations().firstOrNull()
        val mergedStations = radioStationsFromApi.map { radioStation ->
            radioStation.copy(
                isFavorite = favoriteStations?.any {
                    it.id == radioStation.id
                } == true
            )
        }
        return Result.success(mergedStations)
    }

}