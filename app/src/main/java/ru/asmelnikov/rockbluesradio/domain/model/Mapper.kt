package ru.asmelnikov.rockbluesradio.domain.model

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata

fun RadioStation.toMediaItem(): MediaItem {
    val genres = genres.joinToString("|")
    val metadata = MediaMetadata.Builder()
        .setDisplayTitle(name.removeNonAlphanumericFirstChar())
        .setArtworkUri(favicon)
        .setGenre(genres)
        .build()
    return MediaItem.Builder()
        .setUri(url)
        .setMediaId(id)
        .setMediaMetadata(metadata)
        .build()
}

fun String.removeNonAlphanumericFirstChar(): String {
    var output = this
    while (output.isNotEmpty() && !output.first().isLetterOrDigit()) {
        output = output.substring(1)
    }
    return output
}