package ru.asmelnikov.rockbluesradio.playback

import androidx.annotation.OptIn
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import org.koin.android.ext.android.inject
import ru.asmelnikov.rockbluesradio.domain.usecase.ObserveFavoriteStationsUseCase
import ru.asmelnikov.rockbluesradio.domain.usecase.ToggleFavoriteUseCase

@OptIn(UnstableApi::class)
class PlaybackService : MediaLibraryService() {

    private val catalog: PlaybackCatalog by inject()
    private val observeFavoriteStations: ObserveFavoriteStationsUseCase by inject()
    private val toggleFavorite: ToggleFavoriteUseCase by inject()

    private lateinit var player: ExoPlayer
    private var mediaLibrarySession: MediaLibrarySession? = null

    override fun onCreate() {
        super.onCreate()
        player = PlaybackPlayerFactory.create(this)

        val callback = PlaybackLibrarySessionCallback(
            context = this,
            scope = lifecycleScope,
            player = player,
            catalog = catalog,
            toggleFavorite = toggleFavorite,
            sessionProvider = { mediaLibrarySession },
        )
        mediaLibrarySession = MediaLibrarySession.Builder(this, player, callback)
            .setPeriodicPositionUpdateEnabled(false)
            .setSessionActivity(PlaybackSessionActivityFactory.create(this))
            .build()

        PlaybackServiceCoordinator(
            context = this,
            scope = lifecycleScope,
            player = player,
            catalog = catalog,
            observeFavoriteStations = observeFavoriteStations,
            sessionProvider = { mediaLibrarySession },
        ).start()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? =
        mediaLibrarySession

    override fun onDestroy() {
        mediaLibrarySession?.run {
            player.release()
            release()
        }
        mediaLibrarySession = null
        super.onDestroy()
    }
}
