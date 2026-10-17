package ru.asmelnikov.rockbluesradio.data.local.asset

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import ru.asmelnikov.rockbluesradio.domain.model.Genre

class StationsAssetDataSource(private val context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val cache = mutableMapOf<Genre, List<RadioStationDto>>()
    private val cacheMutex = Mutex()

    suspend fun getStations(genre: Genre): List<RadioStationDto> = cacheMutex.withLock {
        cache.getOrPut(genre) { readStations(genre) }
    }

    private suspend fun readStations(genre: Genre): List<RadioStationDto> = withContext(Dispatchers.IO) {
        val content = context.assets.open(genre.assetFileName).bufferedReader().use { it.readText() }
        json.decodeFromString<List<RadioStationDto>>(content)
    }

    private val Genre.assetFileName: String
        get() = when (this) {
            Genre.Rock -> "rock_data.json"
            Genre.Blues -> "blues_data.json"
        }
}
