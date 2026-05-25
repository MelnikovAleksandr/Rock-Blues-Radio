package ru.asmelnikov.rockbluesradio.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation
import ru.asmelnikov.rockbluesradio.domain.repository.RadioStationRepository

class GetFavoriteRadioStationsUseCase(
    private val repository: RadioStationRepository
) {
    fun execute(): Flow<List<RadioStation>> = repository.getFavoriteRadioStations()
}