package ru.asmelnikov.rockbluesradio.presentation.service

import android.app.PendingIntent
import android.content.Intent
import androidx.annotation.OptIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionError
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.flow.firstOrNull
import org.koin.android.ext.android.inject
import ru.asmelnikov.rockbluesradio.R
import ru.asmelnikov.rockbluesradio.domain.model.Genre
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation
import ru.asmelnikov.rockbluesradio.domain.model.toMediaItem
import ru.asmelnikov.rockbluesradio.domain.usecase.GetFavoriteRadioStationsUseCase
import ru.asmelnikov.rockbluesradio.domain.usecase.GetRadioStationsUseCase
import ru.asmelnikov.rockbluesradio.presentation.MainActivity
import androidx.core.net.toUri

@ExperimentalMaterial3Api
@OptIn(UnstableApi::class)
class PlaybackService : MediaLibraryService() {
    private var mediaLibrarySession: MediaLibrarySession? = null
    private lateinit var player: ExoPlayer
    private val getRadioStationsUseCase: GetRadioStationsUseCase by inject()
    private val getFavoriteRadioStationsUseCase: GetFavoriteRadioStationsUseCase by inject()
    private var rockStations = listOf<RadioStation>()
    private var bluesStations = listOf<RadioStation>()
    private var favoriteStations = listOf<RadioStation>()

    companion object {
        private const val ROOT_ID = "root"
        private const val ROCK_STATIONS = "rock"
        private const val BLUES_STATIONS = "blues"
        private const val FAVORITES = "favorites"
    }

    override fun onCreate() {
        super.onCreate()
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()

        player = ExoPlayer.Builder(this)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .build()

        getStations()

        mediaLibrarySession = MediaLibrarySession.Builder(this, player, LibrarySessionCallback())
            .setPeriodicPositionUpdateEnabled(false)
            .setSessionActivity(getSingleTopActivity())
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        return mediaLibrarySession
    }

    override fun onDestroy() {
        mediaLibrarySession?.run {
            player.release()
            release()
        }
        mediaLibrarySession = null
        super.onDestroy()
    }

    private fun getSingleTopActivity(): PendingIntent {
        return PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun getStations() {
        kotlinx.coroutines.runBlocking {
            try {
                getRadioStationsUseCase.execute(Genre.Rock).firstOrNull()?.let { stations ->
                    rockStations = stations
                }

                getRadioStationsUseCase.execute(Genre.Blues).firstOrNull()?.let { stations ->
                    bluesStations = stations
                }

                getFavoriteRadioStationsUseCase.execute().firstOrNull()?.let { favorites ->
                    favoriteStations = favorites
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private inner class LibrarySessionCallback : MediaLibrarySession.Callback {

        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): MediaSession.ConnectionResult {
            val connectionResult = super.onConnect(session, controller)
            return MediaSession.ConnectionResult.accept(
                connectionResult.availableSessionCommands,
                connectionResult.availablePlayerCommands
            )
        }

        override fun onGetLibraryRoot(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<MediaItem>> {
            return Futures.immediateFuture(
                LibraryResult.ofItem(
                    createFolder(
                        ROOT_ID,
                        ""
                    ),
                    params
                )
            )
        }

        override fun onGetChildren(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            parentId: String,
            page: Int,
            pageSize: Int,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
            val children = when (parentId) {
                ROOT_ID -> {
                    buildList {
                        add(
                            createFolder(
                                ROCK_STATIONS,
                                this@PlaybackService.getString(R.string.rock),
                                R.drawable.guitar_auto
                            )
                        )
                        add(
                            createFolder(
                                BLUES_STATIONS,
                                this@PlaybackService.getString(R.string.blues),
                                R.drawable.sax_auto
                            )
                        )
                        if (favoriteStations.isNotEmpty()) {
                            add(
                                createFolder(
                                    FAVORITES,
                                    this@PlaybackService.getString(R.string.favorite),
                                    R.drawable.favourites_auto
                                )
                            )
                        }
                    }
                }

                ROCK_STATIONS -> rockStations.map { it.toMediaItem(ROCK_STATIONS) }
                BLUES_STATIONS -> bluesStations.map { it.toMediaItem(BLUES_STATIONS) }
                FAVORITES -> favoriteStations.map { it.toMediaItem(FAVORITES) }
                else -> emptyList()
            }

            return Futures.immediateFuture(
                LibraryResult.ofItemList(ImmutableList.copyOf(children), params)
            )
        }

        override fun onGetItem(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            mediaId: String
        ): ListenableFuture<LibraryResult<MediaItem>> {
            val stationId = mediaId.substringAfter("_")
            val station = rockStations.find { it.id == stationId }
                ?: bluesStations.find { it.id == stationId }
                ?: favoriteStations.find { it.id == stationId }

            return if (station != null) {
                Futures.immediateFuture(LibraryResult.ofItem(station.toMediaItem(), null))
            } else {
                Futures.immediateFuture(LibraryResult.ofError(SessionError.ERROR_BAD_VALUE))
            }
        }

        override fun onAddMediaItems(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: MutableList<MediaItem>
        ): ListenableFuture<MutableList<MediaItem>> {
            val requestedMediaId = mediaItems.firstOrNull()?.mediaId
                ?: return Futures.immediateFuture(mutableListOf())

            val selectedList = when {
                requestedMediaId.startsWith("${ROCK_STATIONS}_") -> rockStations
                requestedMediaId.startsWith("${BLUES_STATIONS}_") -> bluesStations
                requestedMediaId.startsWith("${FAVORITES}_") -> favoriteStations
                else -> {
                    return Futures.immediateFuture(mediaItems)
                }
            }

            val stationId = requestedMediaId.substringAfter("_")
            val selectedIndex = selectedList.indexOfFirst { it.id == stationId }

            if (selectedIndex == -1) {
                return Futures.immediateFuture(mutableListOf())
            }

            val reorderedStations = selectedList.subList(selectedIndex, selectedList.size) +
                    selectedList.subList(0, selectedIndex)

            player.repeatMode = androidx.media3.common.Player.REPEAT_MODE_ALL

            val prefix = when (selectedList) {
                rockStations -> ROCK_STATIONS
                bluesStations -> BLUES_STATIONS
                favoriteStations -> FAVORITES
                else -> ""
            }

            val resultItems = reorderedStations.map { it.toMediaItem(prefix) }.toMutableList()
            return Futures.immediateFuture(resultItems)
        }

    }

    private fun createFolder(mediaId: String, title: String, iconResId: Int? = null): MediaItem {
        val metadataBuilder = MediaMetadata.Builder()
            .setTitle(title)
            .setIsPlayable(false)
            .setIsBrowsable(true)
            .setMediaType(MediaMetadata.MEDIA_TYPE_FOLDER_RADIO_STATIONS)

        iconResId?.let { resId ->
            val artworkUri = "android.resource://$packageName/$resId".toUri()
            metadataBuilder.setArtworkUri(artworkUri)
        }

        return MediaItem.Builder()
            .setMediaId(mediaId)
            .setMediaMetadata(metadataBuilder.build())
            .build()
    }
}