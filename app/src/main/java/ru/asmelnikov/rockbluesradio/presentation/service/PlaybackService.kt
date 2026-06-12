package ru.asmelnikov.rockbluesradio.presentation.service

import androidx.annotation.OptIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import org.koin.android.ext.android.inject
import ru.asmelnikov.rockbluesradio.domain.usecase.AddToOrRemoveFromFavoritesUseCase
import ru.asmelnikov.rockbluesradio.domain.usecase.GetFavoriteRadioStationsUseCase
import ru.asmelnikov.rockbluesradio.domain.usecase.GetRadioStationsUseCase

@ExperimentalMaterial3Api
@OptIn(UnstableApi::class)
class PlaybackService : MediaLibraryService() {
    private var mediaLibrarySession: MediaLibrarySession? = null
    private lateinit var player: ExoPlayer
    private val getRadioStationsUseCase: GetRadioStationsUseCase by inject()
    private val getFavoriteRadioStationsUseCase: GetFavoriteRadioStationsUseCase by inject()
    private val addToOrRemoveFromFavoritesUseCase: AddToOrRemoveFromFavoritesUseCase by inject()
    private val catalog = PlaybackCatalog()
    private lateinit var coordinator: PlaybackServiceCoordinator

    override fun onCreate() {
        super.onCreate()
        player = PlaybackPlayerFactory.create(this)
        coordinator = PlaybackServiceCoordinator(
            service = this,
            scope = lifecycleScope,
            player = player,
            catalog = catalog,
            getRadioStationsUseCase = getRadioStationsUseCase,
            getFavoriteRadioStationsUseCase = getFavoriteRadioStationsUseCase,
            sessionProvider = { mediaLibrarySession }
        )
        coordinator.initializeCatalog()

        mediaLibrarySession = MediaLibrarySession.Builder(
            this,
            player,
            PlaybackLibrarySessionCallback(
                context = this,
                scope = lifecycleScope,
                player = player,
                catalog = catalog,
                getFavoriteRadioStationsUseCase = getFavoriteRadioStationsUseCase,
                addToOrRemoveFromFavoritesUseCase = addToOrRemoveFromFavoritesUseCase,
                sessionProvider = { mediaLibrarySession }
            )
        )
            .setPeriodicPositionUpdateEnabled(false)
            .setSessionActivity(PlaybackSessionActivityFactory.create(this))
            .build()

        coordinator.observeFavorites()
        coordinator.observePlayerTransitions()
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
}