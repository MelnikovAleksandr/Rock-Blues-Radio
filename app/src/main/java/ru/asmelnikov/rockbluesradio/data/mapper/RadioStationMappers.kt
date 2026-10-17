package ru.asmelnikov.rockbluesradio.data.mapper

import java.util.Locale
import ru.asmelnikov.rockbluesradio.data.local.asset.RadioStationDto
import ru.asmelnikov.rockbluesradio.data.local.database.FavoriteStationEntity
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation

private const val MAX_GENRE_LENGTH = 10
private const val MAX_GENRE_COUNT = 4
private const val TAGS_SEPARATOR = ","

fun RadioStationDto.toDomain(): RadioStation = radioStation(
    id = stationUuid,
    name = name,
    streamUrl = urlResolved,
    faviconUrl = favicon,
    tags = tags,
    isFavorite = false,
)

fun FavoriteStationEntity.toDomain(): RadioStation = radioStation(
    id = stationUuid,
    name = name,
    streamUrl = urlResolved,
    faviconUrl = favicon,
    tags = tags,
    isFavorite = true,
)

fun RadioStation.toFavoriteEntity(): FavoriteStationEntity = FavoriteStationEntity(
    favicon = faviconUrl,
    name = name,
    stationUuid = id,
    tags = genres.joinToString(TAGS_SEPARATOR),
    urlResolved = streamUrl,
)

private fun radioStation(
    id: String,
    name: String?,
    streamUrl: String?,
    faviconUrl: String?,
    tags: String?,
    isFavorite: Boolean,
) = RadioStation(
    id = id,
    name = name?.trimLeadingNonAlphanumeric().orEmpty(),
    streamUrl = streamUrl.orEmpty(),
    faviconUrl = faviconUrl.orEmpty(),
    isFavorite = isFavorite,
    genres = parseGenres(tags),
)

private fun parseGenres(tags: String?): List<String> = tags?.split(TAGS_SEPARATOR).orEmpty()
    .filter { it.length <= MAX_GENRE_LENGTH }
    .take(MAX_GENRE_COUNT)
    .map { genre -> genre.replaceFirstChar { it.titlecase(Locale.getDefault()) } }

private fun String.trimLeadingNonAlphanumeric(): String = dropWhile { char -> char.isLetterOrDigit().not() }
