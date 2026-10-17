package ru.asmelnikov.rockbluesradio.playback

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import kotlinx.coroutines.flow.firstOrNull
import ru.asmelnikov.rockbluesradio.R
import ru.asmelnikov.rockbluesradio.domain.model.Genre
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation
import ru.asmelnikov.rockbluesradio.domain.usecase.ObserveFavoriteStationsUseCase
import ru.asmelnikov.rockbluesradio.domain.usecase.ObserveStationsByGenreUseCase

class PlaybackCatalog(
    private val observeStationsByGenre: ObserveStationsByGenreUseCase,
    private val observeFavoriteStations: ObserveFavoriteStationsUseCase,
) {
    var rockStations: List<RadioStation> = emptyList()
        private set
    var bluesStations: List<RadioStation> = emptyList()
        private set
    var favoriteStations: List<RadioStation> = emptyList()
        private set

    suspend fun load() {
        rockStations = observeStationsByGenre(Genre.Rock).firstOrNull().orEmpty()
        bluesStations = observeStationsByGenre(Genre.Blues).firstOrNull().orEmpty()
        refreshFavorites()
    }

    suspend fun refreshFavorites() {
        favoriteStations = observeFavoriteStations().firstOrNull().orEmpty()
    }

    fun setFavorites(favorites: List<RadioStation>) {
        favoriteStations = favorites
    }

    fun shouldRefreshFavorites(parentId: String): Boolean =
        parentId == PlaybackContract.ROOT_ID || parentId == PlaybackContract.FAVORITES

    fun rootChildrenCount(): Int = rootFolders().size

    fun childCount(parentId: String): Int = when (parentId) {
        PlaybackContract.ROOT_ID -> rootChildrenCount()
        else -> stationsIn(parentId)?.size ?: 0
    }

    fun childrenForParent(context: Context, parentId: String): List<MediaItem> = when (parentId) {
        PlaybackContract.ROOT_ID -> rootFolders().map { it.toMediaItem(context) }
        else -> stationsIn(parentId)?.map { it.toMediaItem(parentId) }.orEmpty()
    }

    fun stationsIn(folderId: String): List<RadioStation>? = when (folderId) {
        PlaybackContract.ROCK_STATIONS -> rockStations
        PlaybackContract.BLUES_STATIONS -> bluesStations
        PlaybackContract.FAVORITES -> favoriteStations
        else -> null
    }

    fun findStationByMediaId(mediaId: String): RadioStation? {
        val stationId = PlaybackContract.stationId(mediaId)
        return rockStations.find { it.id == stationId }
            ?: bluesStations.find { it.id == stationId }
            ?: favoriteStations.find { it.id == stationId }
    }

    private fun rootFolders(): List<Folder> = buildList {
        add(Folder(PlaybackContract.ROCK_STATIONS, R.string.rock, R.drawable.guitar_auto))
        add(Folder(PlaybackContract.BLUES_STATIONS, R.string.blues, R.drawable.sax_auto))
        if (favoriteStations.isNotEmpty()) {
            add(Folder(PlaybackContract.FAVORITES, R.string.favorite, R.drawable.favourites_auto))
        }
    }

    private data class Folder(val mediaId: String, @StringRes val titleRes: Int, @DrawableRes val iconRes: Int) {
        fun toMediaItem(context: Context): MediaItem {
            val metadata = MediaMetadata.Builder()
                .setTitle(context.getString(titleRes))
                .setIsPlayable(false)
                .setIsBrowsable(true)
                .setMediaType(MediaMetadata.MEDIA_TYPE_FOLDER_RADIO_STATIONS)
                .setArtworkUri("android.resource://${context.packageName}/$iconRes".toUri())
                .build()
            return MediaItem.Builder()
                .setMediaId(mediaId)
                .setMediaMetadata(metadata)
                .build()
        }
    }
}
