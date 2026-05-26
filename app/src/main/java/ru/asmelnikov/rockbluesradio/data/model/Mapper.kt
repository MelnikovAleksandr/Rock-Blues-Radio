package ru.asmelnikov.rockbluesradio.data.model

import android.net.Uri
import androidx.core.net.toUri
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation
import java.util.Locale

object GenreConstants {
    const val MAX_GENRE_CHAR_LENGTH = 10
    const val MAX_GENRE_COUNT = 4
}

fun RadioStationDtoItem.map(isFavorite: Boolean): RadioStation {
    val genres = (tags?.split(",") ?: emptyList())
        .filter { genre ->
            genre.length <= GenreConstants.MAX_GENRE_CHAR_LENGTH
        }
        .take(GenreConstants.MAX_GENRE_COUNT)
        .map { genre ->
            genre.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        }
    return RadioStation(
        id = stationUuid,
        name = name?.removeNonAlphanumericFirstChar() ?: "",
        url = urlResolved?.toUri() ?: Uri.EMPTY,
        favicon = favicon?.toUri() ?: Uri.EMPTY,
        isFavorite = isFavorite,
        genres = genres
    )
}

fun RadioStation.mapToDto(): RadioStationDtoItem {
    return RadioStationDtoItem(
        urlResolved = url.toString(),
        stationUuid = id,
        favicon = favicon.toString(),
        name = name,
        tags = genres.joinToString(",")
    )
}

fun String.removeNonAlphanumericFirstChar(): String {
    var output = this
    while (output.isNotEmpty() && !output.first().isLetterOrDigit()) {
        output = output.substring(1)
    }
    return output
}