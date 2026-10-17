package ru.asmelnikov.rockbluesradio.playback

import android.content.Context
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService.LibraryParams
import androidx.media3.session.MediaLibraryService.MediaLibrarySession
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionError
import androidx.media3.session.SessionResult
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.asmelnikov.rockbluesradio.domain.usecase.ToggleFavoriteUseCase

@OptIn(UnstableApi::class)
class PlaybackLibrarySessionCallback(
    private val context: Context,
    private val scope: CoroutineScope,
    private val player: Player,
    private val catalog: PlaybackCatalog,
    private val toggleFavorite: ToggleFavoriteUseCase,
    private val sessionProvider: () -> MediaLibrarySession?,
) : MediaLibrarySession.Callback {

    override fun onConnect(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
    ): MediaSession.ConnectionResult {
        val defaultResult = super.onConnect(session, controller)
        val sessionCommands = defaultResult.availableSessionCommands
            .buildUpon()
            .add(FavoriteCommandButton.sessionCommand)
            .build()

        return MediaSession.ConnectionResult.AcceptedResultBuilder(session)
            .setAvailableSessionCommands(sessionCommands)
            .setAvailablePlayerCommands(defaultResult.availablePlayerCommands)
            .setMediaButtonPreferences(favoriteButtonPreferences())
            .build()
    }

    override fun onCustomCommand(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        customCommand: SessionCommand,
        args: Bundle,
    ): ListenableFuture<SessionResult> {
        if (customCommand.customAction != PlaybackContract.COMMAND_TOGGLE_FAVORITE) {
            return super.onCustomCommand(session, controller, customCommand, args)
        }

        val station = player.currentMediaItem?.mediaId?.let(catalog::findStationByMediaId)
            ?: return Futures.immediateFuture(SessionResult(SessionError.ERROR_BAD_VALUE))

        scope.launch {
            toggleFavorite(station)
            catalog.refreshFavorites()
            sessionProvider()?.run {
                notifyChildrenChanged(PlaybackContract.FAVORITES, catalog.favoriteStations.size, null)
                notifyChildrenChanged(PlaybackContract.ROOT_ID, catalog.rootChildrenCount(), null)
                setMediaButtonPreferences(favoriteButtonPreferences())
            }
        }

        return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
    }

    override fun onSubscribe(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        parentId: String,
        params: LibraryParams?,
    ): ListenableFuture<LibraryResult<Void>> {
        if (catalog.shouldRefreshFavorites(parentId)) {
            scope.launch {
                catalog.refreshFavorites()
                sessionProvider()?.notifyChildrenChanged(parentId, catalog.childCount(parentId), params)
            }
        }
        return Futures.immediateFuture(LibraryResult.ofVoid())
    }

    override fun onGetLibraryRoot(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        params: LibraryParams?,
    ): ListenableFuture<LibraryResult<MediaItem>> {
        val rootMetadata = MediaMetadata.Builder()
            .setTitle("")
            .setIsPlayable(false)
            .setIsBrowsable(true)
            .setMediaType(MediaMetadata.MEDIA_TYPE_FOLDER_RADIO_STATIONS)
            .build()
        val rootItem = MediaItem.Builder()
            .setMediaId(PlaybackContract.ROOT_ID)
            .setMediaMetadata(rootMetadata)
            .build()
        return Futures.immediateFuture(LibraryResult.ofItem(rootItem, params))
    }

    override fun onGetChildren(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        parentId: String,
        page: Int,
        pageSize: Int,
        params: LibraryParams?,
    ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
        if (catalog.shouldRefreshFavorites(parentId)) {
            scope.launch { catalog.refreshFavorites() }
        }

        val children = catalog.childrenForParent(context, parentId)
        return Futures.immediateFuture(LibraryResult.ofItemList(ImmutableList.copyOf(children), params))
    }

    override fun onGetItem(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        mediaId: String,
    ): ListenableFuture<LibraryResult<MediaItem>> {
        val station = catalog.findStationByMediaId(mediaId)
        val result = if (station != null) {
            LibraryResult.ofItem(station.toMediaItem(), null)
        } else {
            LibraryResult.ofError(SessionError.ERROR_BAD_VALUE)
        }
        return Futures.immediateFuture(result)
    }

    override fun onAddMediaItems(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaItems: MutableList<MediaItem>,
    ): ListenableFuture<MutableList<MediaItem>> {
        val requestedMediaId = mediaItems.firstOrNull()?.mediaId
            ?: return Futures.immediateFuture(mutableListOf())
        val folderId = PlaybackContract.folderId(requestedMediaId)
            ?: return Futures.immediateFuture(mediaItems)
        val folderStations = catalog.stationsIn(folderId).orEmpty()

        val stationId = PlaybackContract.stationId(requestedMediaId)
        val selectedIndex = folderStations.indexOfFirst { it.id == stationId }
        if (selectedIndex == -1) {
            return Futures.immediateFuture(mutableListOf())
        }

        val reorderedStations = folderStations.drop(selectedIndex) + folderStations.take(selectedIndex)
        player.repeatMode = Player.REPEAT_MODE_ALL
        val resultItems = reorderedStations.map { it.toMediaItem(folderId) }.toMutableList()
        return Futures.immediateFuture(resultItems)
    }

    private fun favoriteButtonPreferences() =
        FavoriteCommandButton.preferences(context, player.currentMediaItem, catalog.favoriteStations)
}
