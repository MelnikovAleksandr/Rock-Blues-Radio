package ru.asmelnikov.rockbluesradio.playback

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaLibraryService.MediaLibrarySession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.asmelnikov.rockbluesradio.domain.usecase.ObserveFavoriteStationsUseCase

class PlaybackServiceCoordinator(
    private val context: Context,
    private val scope: CoroutineScope,
    private val player: Player,
    private val catalog: PlaybackCatalog,
    private val observeFavoriteStations: ObserveFavoriteStationsUseCase,
    private val sessionProvider: () -> MediaLibrarySession?,
) {
    fun start() {
        loadCatalog()
        observeFavorites()
        observeMediaItemTransitions()
    }

    @OptIn(UnstableApi::class)
    fun updateFavoriteButton() {
        sessionProvider()?.setMediaButtonPreferences(
            FavoriteCommandButton.preferences(context, player.currentMediaItem, catalog.favoriteStations),
        )
    }

    private fun loadCatalog() {
        scope.launch {
            catalog.load()
            updateFavoriteButton()
        }
    }

    private fun observeFavorites() {
        scope.launch {
            observeFavoriteStations().collectLatest { favorites ->
                val rootCountBefore = catalog.rootChildrenCount()
                catalog.setFavorites(favorites)

                val session = sessionProvider()
                session?.notifyChildrenChanged(PlaybackContract.FAVORITES, favorites.size, null)
                val rootCountAfter = catalog.rootChildrenCount()
                if (rootCountBefore != rootCountAfter) {
                    session?.notifyChildrenChanged(PlaybackContract.ROOT_ID, rootCountAfter, null)
                }
                updateFavoriteButton()
            }
        }
    }

    private fun observeMediaItemTransitions() {
        player.addListener(
            object : Player.Listener {
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    updateFavoriteButton()
                }
            },
        )
    }
}
