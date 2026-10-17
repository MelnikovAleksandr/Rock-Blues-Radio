package ru.asmelnikov.rockbluesradio.data.local.asset

import ru.asmelnikov.rockbluesradio.domain.model.Genre

interface StationsDataSource {
    suspend fun getStations(genre: Genre): List<RadioStationDto>
}
