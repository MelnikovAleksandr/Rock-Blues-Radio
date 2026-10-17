package ru.asmelnikov.rockbluesradio.domain.usecase

import ru.asmelnikov.rockbluesradio.domain.model.RadioStation
import ru.asmelnikov.rockbluesradio.domain.repository.RadioStationRepository

class ToggleFavoriteUseCase(private val repository: RadioStationRepository) {

    suspend operator fun invoke(station: RadioStation) {
        if (repository.isFavorite(station.id)) {
            repository.removeFromFavorites(station)
        } else {
            repository.addToFavorites(station)
        }
    }
}
