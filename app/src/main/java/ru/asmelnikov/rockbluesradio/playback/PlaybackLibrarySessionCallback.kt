package ru.asmelnikov.rockbluesradio.playback

import android.content.Context
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
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
    private val itemFactory: LibraryItemFactory,
    private val toggleFavorite: ToggleFavoriteUseCase,
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
            .setMediaButtonPreferences(
                FavoriteCommandButton.preferences(context, player.currentMediaItem, catalog.favoriteStations),
            )
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

        val station = player.currentMediaItem?.mediaId?.let(catalog::locate)?.station
            ?: return Futures.immediateFuture(SessionResult(SessionError.ERROR_BAD_VALUE))
        scope.launch { toggleFavorite(station) }
        return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
    }

    override fun onGetLibraryRoot(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        params: LibraryParams?,
    ): ListenableFuture<LibraryResult<MediaItem>> = Futures.immediateFuture(
        LibraryResult.ofItem(itemFactory.root(), params),
    )

    override fun onGetChildren(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        parentId: String,
        page: Int,
        pageSize: Int,
        params: LibraryParams?,
    ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> = scope.launchFuture {
        catalog.awaitLoaded()
        LibraryResult.ofItemList(ImmutableList.copyOf(itemFactory.children(catalog, parentId)), params)
    }

    override fun onGetItem(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        mediaId: String,
    ): ListenableFuture<LibraryResult<MediaItem>> = scope.launchFuture {
        catalog.awaitLoaded()
        catalog.locate(mediaId)
            ?.let { LibraryResult.ofItem(itemFactory.station(it), null) }
            ?: LibraryResult.ofError(SessionError.ERROR_BAD_VALUE)
    }

    override fun onAddMediaItems(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaItems: MutableList<MediaItem>,
    ): ListenableFuture<MutableList<MediaItem>> {
        if (mediaItems.all { it.localConfiguration != null }) {
            return Futures.immediateFuture(mediaItems)
        }
        return scope.launchFuture {
            catalog.awaitLoaded()
            val requestedItem = mediaItems.singleOrNull()
            if (requestedItem != null) {
                queueFor(requestedItem.mediaId)
            } else {
                mediaItems.mapNotNull { item -> item.takeIf { it.localConfiguration != null } ?: resolve(item.mediaId) }
            }.toMutableList()
        }
    }

    private fun queueFor(mediaId: String): List<MediaItem> {
        val queue = catalog.queueStartingAt(mediaId) ?: return listOfNotNull(resolve(mediaId))
        player.repeatMode = Player.REPEAT_MODE_ALL
        return itemFactory.queue(queue)
    }

    private fun resolve(mediaId: String): MediaItem? = catalog.locate(mediaId)?.let(itemFactory::station)
}
