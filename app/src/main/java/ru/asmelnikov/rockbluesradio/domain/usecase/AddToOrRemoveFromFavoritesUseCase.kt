package ru.asmelnikov.rockbluesradio.domain.usecase

import ru.asmelnikov.rockbluesradio.domain.model.RadioStation
import ru.asmelnikov.rockbluesradio.domain.repository.RadioStationRepository

class AddToOrRemoveFromFavoritesUseCase(
    private val repository: RadioStationRepository
) {
    suspend fun execute(item: RadioStation) {
        val isAdded = repository.getFavoriteRadioStationByStationUUID(item.id) != null
        if (isAdded)
            repository.removeFromFavorites(item)
        else
            repository.addToFavorites(item)
    }
}