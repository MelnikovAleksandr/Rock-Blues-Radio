package ru.asmelnikov.rockbluesradio.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.asmelnikov.rockbluesradio.domain.repository.RadioStationRepository

class GetFavoritesRadioCountUseCase(
    private val repository: RadioStationRepository
) {
    fun execute(): Flow<Int> = repository.getFavoriteRadioStations()
        .map { stations -> stations.size }
}