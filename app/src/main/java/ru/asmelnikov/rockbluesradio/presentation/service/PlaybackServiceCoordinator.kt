@file:OptIn(ExperimentalMaterial3Api::class)
package ru.asmelnikov.rockbluesradio.presentation.service

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import ru.asmelnikov.rockbluesradio.domain.usecase.GetFavoriteRadioStationsUseCase
import ru.asmelnikov.rockbluesradio.domain.usecase.GetRadioStationsUseCase

@androidx.annotation.OptIn(UnstableApi::class)
class PlaybackServiceCoordinator(
    private val service: PlaybackService,
    private val scope: LifecycleCoroutineScope,
    private val player: ExoPlayer,
    private val catalog: PlaybackCatalog,
    private val getRadioStationsUseCase: GetRadioStationsUseCase,
    private val getFavoriteRadioStationsUseCase: GetFavoriteRadioStationsUseCase,
    private val sessionProvider: () -> MediaLibraryService.MediaLibrarySession?
) {
    fun initializeCatalog() {
        scope.launch {
            catalog.loadSnapshot(getRadioStationsUseCase, getFavoriteRadioStationsUseCase)
            catalog.setFavorites(getFavoriteRadioStationsUseCase.execute().firstOrNull().orEmpty())
            updateFavoriteButtonPreferences()
        }
    }

    fun observeFavorites() {
        scope.launch {
            getFavoriteRadioStationsUseCase.execute().collectLatest { favorites ->
                val rootCountBefore = catalog.rootChildrenCount()
                catalog.setFavorites(favorites)

                sessionProvider()?.notifyChildrenChanged(
                    PlaybackContract.FAVORITES,
                    catalog.favoriteStations.size,
                    null
                )

                val rootCountAfter = catalog.rootChildrenCount()
                if (rootCountBefore != rootCountAfter) {
                    sessionProvider()?.notifyChildrenChanged(
                        PlaybackContract.ROOT_ID,
                        rootCountAfter,
                        null
                    )
                }

                updateFavoriteButtonPreferences()
            }
        }
    }

    fun observePlayerTransitions() {
        player.addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                updateFavoriteButtonPreferences()
            }
        })
    }

    fun updateFavoriteButtonPreferences() {
        sessionProvider()?.setMediaButtonPreferences(
            listOf(
                FavoriteButtonProvider.create(
                    context = service,
                    currentMediaItem = player.currentMediaItem,
                    favoriteStations = catalog.favoriteStations
                )
            )
        )
    }
}
