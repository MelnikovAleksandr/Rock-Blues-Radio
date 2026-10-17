package ru.asmelnikov.rockbluesradio.presentation.preview

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation

internal object PreviewData {

    private const val ITEM_COUNT = 10

    fun station(id: String = "station"): RadioStation = RadioStation(
        id = id,
        name = "Lena Peck",
        streamUrl = "",
        faviconUrl = "",
        isFavorite = false,
        genres = listOf("Rock", "Blues"),
    )

    fun stations(): List<RadioStation> = List(ITEM_COUNT) { index -> station(id = "station_$index") }

    fun mediaItem(title: String = "Bohemian Rhapsody - Queen"): MediaItem = MediaItem.Builder()
        .setMediaId("1")
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(title)
                .setDisplayTitle(title)
                .build(),
        )
        .build()

    fun mediaItems(): List<MediaItem> = List(ITEM_COUNT) { mediaItem() }
}
