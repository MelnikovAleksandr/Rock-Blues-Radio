package ru.asmelnikov.rockbluesradio.playback

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.first
import ru.asmelnikov.rockbluesradio.R
import ru.asmelnikov.rockbluesradio.domain.model.Genre
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation
import ru.asmelnikov.rockbluesradio.domain.usecase.ObserveFavoriteStationsUseCase
import ru.asmelnikov.rockbluesradio.domain.usecase.ObserveStationsByGenreUseCase

class PlaybackCatalog(
    private val observeStationsByGenre: ObserveStationsByGenreUseCase,
    private val observeFavoriteStations: ObserveFavoriteStationsUseCase,
) {
    private val loaded = CompletableDeferred<Unit>()
    private val stationsByFolder = mutableMapOf<StationFolder, List<RadioStation>>()

    val favoriteStations: List<RadioStation>
        get() = stations(StationFolder.Favorites)

    suspend fun load() {
        try {
            Genre.entries.forEach { genre ->
                stationsByFolder[StationFolder.of(genre)] = runCatching { observeStationsByGenre(genre).first() }
                    .getOrDefault(emptyList())
            }
            setFavorites(runCatching { observeFavoriteStations().first() }.getOrDefault(emptyList()))
        } finally {
            loaded.complete(Unit)
        }
    }

    suspend fun awaitLoaded() {
        loaded.await()
    }

    fun setFavorites(favorites: List<RadioStation>) {
        stationsByFolder[StationFolder.Favorites] = favorites
    }

    fun stations(folder: StationFolder): List<RadioStation> = stationsByFolder[folder].orEmpty()

    fun childCount(parentId: String): Int = when (parentId) {
        PlaybackContract.ROOT_ID -> rootFolders().size
        else -> StationFolder.fromFolderId(parentId)?.let { stations(it).size } ?: 0
    }

    fun children(context: Context, parentId: String): List<MediaItem> = when (parentId) {
        PlaybackContract.ROOT_ID -> rootFolders().map { it.toFolderMediaItem(context) }
        else -> StationFolder.fromFolderId(parentId)?.let { stations(it).toMediaItems(it) }.orEmpty()
    }

    fun findStation(mediaId: String): RadioStation? = findStationWithFolder(mediaId)?.second

    fun findMediaItem(mediaId: String): MediaItem? =
        findStationWithFolder(mediaId)?.let { (folder, station) -> station.toMediaItem(folder) }

    private fun findStationWithFolder(mediaId: String): Pair<StationFolder, RadioStation>? {
        val stationId = PlaybackContract.stationId(mediaId)
        val searchOrder = listOfNotNull(PlaybackContract.folderOf(mediaId)) + StationFolder.entries
        return searchOrder.firstNotNullOfOrNull { folder ->
            stations(folder).find { it.id == stationId }?.let { folder to it }
        }
    }

    private fun rootFolders(): List<StationFolder> = buildList {
        add(StationFolder.Rock)
        add(StationFolder.Blues)
        if (favoriteStations.isNotEmpty()) add(StationFolder.Favorites)
    }

    private fun StationFolder.toFolderMediaItem(context: Context): MediaItem {
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

    @get:StringRes
    private val StationFolder.titleRes: Int
        get() = when (this) {
            StationFolder.Rock -> R.string.rock
            StationFolder.Blues -> R.string.blues
            StationFolder.Favorites -> R.string.favorite
        }

    @get:DrawableRes
    private val StationFolder.iconRes: Int
        get() = when (this) {
            StationFolder.Rock -> R.drawable.guitar_auto
            StationFolder.Blues -> R.drawable.sax_auto
            StationFolder.Favorites -> R.drawable.favourites_auto
        }
}
