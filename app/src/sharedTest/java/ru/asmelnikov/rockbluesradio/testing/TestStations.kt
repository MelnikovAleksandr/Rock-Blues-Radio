package ru.asmelnikov.rockbluesradio.testing

import ru.asmelnikov.rockbluesradio.domain.model.RadioStation

fun testStation(
    id: String,
    name: String = "Station $id",
    streamUrl: String = "https://stream.example/$id",
    faviconUrl: String = "https://favicon.example/$id.png",
    isFavorite: Boolean = false,
    genres: List<String> = listOf("Rock"),
): RadioStation = RadioStation(
    id = id,
    name = name,
    streamUrl = streamUrl,
    faviconUrl = faviconUrl,
    isFavorite = isFavorite,
    genres = genres,
)

fun testStations(prefix: String, count: Int): List<RadioStation> = List(count) { index ->
    testStation(id = "$prefix-$index")
}
