package ru.asmelnikov.rockbluesradio.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation
import ru.asmelnikov.rockbluesradio.domain.repository.RadioStationRepository

class ObserveFavoriteStationsUseCase(private val repository: RadioStationRepository) {

    operator fun invoke(): Flow<List<RadioStation>> = repository.observeFavoriteStations()
}
