package ru.asmelnikov.rockbluesradio.data.local.asset

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import ru.asmelnikov.rockbluesradio.domain.model.Genre

class StationsAssetDataSource(private val context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    suspend fun getStations(genre: Genre): List<RadioStationDto> = withContext(Dispatchers.IO) {
        val content = context.assets.open(genre.assetFileName).bufferedReader().use { it.readText() }
        json.decodeFromString<List<RadioStationDto>>(content)
    }

    private val Genre.assetFileName: String
        get() = when (this) {
            Genre.Rock -> "rock_data.json"
            Genre.Blues -> "blues_data.json"
        }
}
