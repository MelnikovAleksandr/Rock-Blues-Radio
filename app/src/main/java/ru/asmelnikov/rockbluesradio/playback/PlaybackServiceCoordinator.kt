package ru.asmelnikov.rockbluesradio.playback

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaLibraryService.MediaLibrarySession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation
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
        scope.launch {
            catalog.load()
            notifyAllFoldersChanged()
            updateFavoriteButton()
            observeFavorites()
        }
        observeMediaItemTransitions()
    }

    private suspend fun observeFavorites() {
        observeFavoriteStations().collect { favorites ->
            catalog.setFavorites(favorites)
            syncFavoritesQueue(favorites)
            notifyChildrenChanged(PlaybackContract.ROOT_ID)
            notifyChildrenChanged(StationFolder.Favorites.mediaId)
            updateFavoriteButton()
        }
    }

    private fun syncFavoritesQueue(favorites: List<RadioStation>) {
        if (PlaybackContract.folderOf(player.currentMediaItem?.mediaId) == StationFolder.Favorites) {
            player.replaceQueueAroundCurrentItem(favorites.toMediaItems(StationFolder.Favorites))
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

    private fun notifyAllFoldersChanged() {
        notifyChildrenChanged(PlaybackContract.ROOT_ID)
        StationFolder.entries.forEach { notifyChildrenChanged(it.mediaId) }
    }

    private fun notifyChildrenChanged(parentId: String) {
        sessionProvider()?.notifyChildrenChanged(parentId, catalog.childCount(parentId), null)
    }

    private fun updateFavoriteButton() {
        sessionProvider()?.setMediaButtonPreferences(
            FavoriteCommandButton.preferences(context, player.currentMediaItem, catalog.favoriteStations),
        )
    }
}
