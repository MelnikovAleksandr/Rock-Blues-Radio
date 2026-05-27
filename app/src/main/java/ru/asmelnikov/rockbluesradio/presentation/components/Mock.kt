package ru.asmelnikov.rockbluesradio.presentation.components

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation
import java.util.UUID

fun mockMediaItem(
    title: String = "Bohemian Rhapsody - Queen"
): MediaItem = MediaItem.Builder()
    .setMediaId("1")
    .setMediaMetadata(
        MediaMetadata.Builder()
            .setTitle(title)
            .setDisplayTitle(title)
            .build()
    )
    .build()

fun mockRadioStation(): RadioStation = RadioStation(
    id = "vestibulum",
    name = "Lena Peck",
    url = Uri.EMPTY,
    favicon = Uri.EMPTY,
    isFavorite = false,
    genres = listOf("rock", "blues")
)

fun mockRadioStations(): List<RadioStation> = listOf(
    mockRadioStation().copy(id = UUID.randomUUID().toString()),
    mockRadioStation().copy(id = UUID.randomUUID().toString()),
    mockRadioStation().copy(id = UUID.randomUUID().toString()),
    mockRadioStation().copy(id = UUID.randomUUID().toString()),
    mockRadioStation().copy(id = UUID.randomUUID().toString()),
    mockRadioStation().copy(id = UUID.randomUUID().toString()),
    mockRadioStation().copy(id = UUID.randomUUID().toString()),
    mockRadioStation().copy(id = UUID.randomUUID().toString()),
    mockRadioStation().copy(id = UUID.randomUUID().toString()),
    mockRadioStation().copy(id = UUID.randomUUID().toString())
)