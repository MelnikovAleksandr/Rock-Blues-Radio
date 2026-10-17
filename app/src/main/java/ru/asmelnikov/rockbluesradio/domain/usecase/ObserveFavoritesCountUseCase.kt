package ru.asmelnikov.rockbluesradio.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.asmelnikov.rockbluesradio.domain.repository.RadioStationRepository

class ObserveFavoritesCountUseCase(private val repository: RadioStationRepository) {

    operator fun invoke(): Flow<Int> = repository.observeFavoriteStations().map { it.size }
}
