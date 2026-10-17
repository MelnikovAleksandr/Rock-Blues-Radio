package ru.asmelnikov.rockbluesradio.playback

import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation

private const val GENRES_SEPARATOR = "|"

fun RadioStation.toMediaItem(folderId: String = ""): MediaItem {
    val metadata = MediaMetadata.Builder()
        .setDisplayTitle(name)
        .setArtworkUri(faviconUrl.toUri())
        .setGenre(genres.joinToString(GENRES_SEPARATOR))
        .setIsBrowsable(false)
        .setIsPlayable(true)
        .build()
    return MediaItem.Builder()
        .setUri(streamUrl)
        .setMediaId(PlaybackContract.mediaId(folderId, id))
        .setMediaMetadata(metadata)
        .build()
}
