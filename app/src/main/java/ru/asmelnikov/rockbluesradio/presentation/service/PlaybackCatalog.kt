package ru.asmelnikov.rockbluesradio.presentation.service

import android.content.Context
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import kotlinx.coroutines.flow.firstOrNull
import ru.asmelnikov.rockbluesradio.R
import ru.asmelnikov.rockbluesradio.domain.model.Genre
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation
import ru.asmelnikov.rockbluesradio.domain.model.toMediaItem
import ru.asmelnikov.rockbluesradio.domain.usecase.GetFavoriteRadioStationsUseCase
import ru.asmelnikov.rockbluesradio.domain.usecase.GetRadioStationsUseCase

class PlaybackCatalog {
    var rockStations: List<RadioStation> = emptyList()
        private set
    var bluesStations: List<RadioStation> = emptyList()
        private set
    var favoriteStations: List<RadioStation> = emptyList()
        private set

    suspend fun loadSnapshot(
        getRadioStationsUseCase: GetRadioStationsUseCase,
        getFavoriteRadioStationsUseCase: GetFavoriteRadioStationsUseCase
    ) {
        rockStations = getRadioStationsUseCase.execute(Genre.Rock).firstOrNull().orEmpty()
        bluesStations = getRadioStationsUseCase.execute(Genre.Blues).firstOrNull().orEmpty()
        favoriteStations = getFavoriteRadioStationsUseCase.execute().firstOrNull().orEmpty()
    }

    fun setFavorites(favorites: List<RadioStation>) {
        favoriteStations = favorites
    }

    fun rootChildrenCount(): Int = if (favoriteStations.isNotEmpty()) 3 else 2

    fun shouldRefreshFavorites(parentId: String): Boolean {
        return parentId == PlaybackContract.ROOT_ID || parentId == PlaybackContract.FAVORITES
    }

    fun childCount(parentId: String): Int {
        return when (parentId) {
            PlaybackContract.ROOT_ID -> rootChildrenCount()
            PlaybackContract.FAVORITES -> favoriteStations.size
            PlaybackContract.ROCK_STATIONS -> rockStations.size
            PlaybackContract.BLUES_STATIONS -> bluesStations.size
            else -> 0
        }
    }

    fun childrenForParent(context: Context, parentId: String): List<MediaItem> {
        return when (parentId) {
            PlaybackContract.ROOT_ID -> rootChildren(context)
            PlaybackContract.ROCK_STATIONS -> rockStations.map { it.toMediaItem(PlaybackContract.ROCK_STATIONS) }
            PlaybackContract.BLUES_STATIONS -> bluesStations.map { it.toMediaItem(PlaybackContract.BLUES_STATIONS) }
            PlaybackContract.FAVORITES -> favoriteStations.map { it.toMediaItem(PlaybackContract.FAVORITES) }
            else -> emptyList()
        }
    }

    fun findStationByMediaId(mediaId: String): RadioStation? {
        val stationId = mediaId.substringAfter("_")
        return rockStations.find { it.id == stationId }
            ?: bluesStations.find { it.id == stationId }
            ?: favoriteStations.find { it.id == stationId }
    }

    fun resolveListByRequestedMediaId(requestedMediaId: String): List<RadioStation>? {
        return when {
            requestedMediaId.startsWith("${PlaybackContract.ROCK_STATIONS}_") -> rockStations
            requestedMediaId.startsWith("${PlaybackContract.BLUES_STATIONS}_") -> bluesStations
            requestedMediaId.startsWith("${PlaybackContract.FAVORITES}_") -> favoriteStations
            else -> null
        }
    }

    fun prefixForList(selectedList: List<RadioStation>): String {
        return when (selectedList) {
            rockStations -> PlaybackContract.ROCK_STATIONS
            bluesStations -> PlaybackContract.BLUES_STATIONS
            favoriteStations -> PlaybackContract.FAVORITES
            else -> ""
        }
    }

    private fun rootChildren(context: Context): List<MediaItem> {
        return buildList {
            add(
                createFolder(
                    context = context,
                    mediaId = PlaybackContract.ROCK_STATIONS,
                    title = context.getString(R.string.rock),
                    iconResId = R.drawable.guitar_auto
                )
            )
            add(
                createFolder(
                    context = context,
                    mediaId = PlaybackContract.BLUES_STATIONS,
                    title = context.getString(R.string.blues),
                    iconResId = R.drawable.sax_auto
                )
            )
            if (favoriteStations.isNotEmpty()) {
                add(
                    createFolder(
                        context = context,
                        mediaId = PlaybackContract.FAVORITES,
                        title = context.getString(R.string.favorite),
                        iconResId = R.drawable.favourites_auto
                    )
                )
            }
        }
    }

    private fun createFolder(
        context: Context,
        mediaId: String,
        title: String,
        iconResId: Int? = null
    ): MediaItem {
        val metadataBuilder = MediaMetadata.Builder()
            .setTitle(title)
            .setIsPlayable(false)
            .setIsBrowsable(true)
            .setMediaType(MediaMetadata.MEDIA_TYPE_FOLDER_RADIO_STATIONS)

        iconResId?.let { resId ->
            val artworkUri = "android.resource://${context.packageName}/$resId".toUri()
            metadataBuilder.setArtworkUri(artworkUri)
        }

        return MediaItem.Builder()
            .setMediaId(mediaId)
            .setMediaMetadata(metadataBuilder.build())
            .build()
    }
}
