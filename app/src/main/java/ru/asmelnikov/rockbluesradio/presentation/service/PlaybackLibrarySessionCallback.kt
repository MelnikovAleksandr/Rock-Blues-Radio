package ru.asmelnikov.rockbluesradio.presentation.service

import android.content.Context
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionError
import androidx.media3.session.SessionResult
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import ru.asmelnikov.rockbluesradio.domain.model.toMediaItem
import ru.asmelnikov.rockbluesradio.domain.usecase.AddToOrRemoveFromFavoritesUseCase
import ru.asmelnikov.rockbluesradio.domain.usecase.GetFavoriteRadioStationsUseCase
@OptIn(UnstableApi::class)
class PlaybackLibrarySessionCallback(
    private val context: Context,
    private val scope: CoroutineScope,
    private val player: ExoPlayer,
    private val catalog: PlaybackCatalog,
    private val getFavoriteRadioStationsUseCase: GetFavoriteRadioStationsUseCase,
    private val addToOrRemoveFromFavoritesUseCase: AddToOrRemoveFromFavoritesUseCase,
    private val sessionProvider: () -> MediaLibraryService.MediaLibrarySession?
) : MediaLibraryService.MediaLibrarySession.Callback {

    override fun onConnect(
        session: MediaSession,
        controller: MediaSession.ControllerInfo
    ): MediaSession.ConnectionResult {
        val connectionResult = super.onConnect(session, controller)
        val sessionCommands = connectionResult.availableSessionCommands
            .buildUpon()
            .add(SessionCommand(PlaybackContract.COMMAND_TOGGLE_FAVORITE, Bundle.EMPTY))
            .build()

        return MediaSession.ConnectionResult.AcceptedResultBuilder(session)
            .setAvailableSessionCommands(sessionCommands)
            .setAvailablePlayerCommands(connectionResult.availablePlayerCommands)
            .setMediaButtonPreferences(
                listOf(
                    FavoriteButtonProvider.create(
                        context = context,
                        currentMediaItem = player.currentMediaItem,
                        favoriteStations = catalog.favoriteStations
                    )
                )
            )
            .build()
    }

    override fun onCustomCommand(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        customCommand: SessionCommand,
        args: Bundle
    ): ListenableFuture<SessionResult> {
        if (customCommand.customAction != PlaybackContract.COMMAND_TOGGLE_FAVORITE) {
            return super.onCustomCommand(session, controller, customCommand, args)
        }

        val mediaId = player.currentMediaItem?.mediaId
            ?: return Futures.immediateFuture(SessionResult(SessionError.ERROR_BAD_VALUE))
        val station = catalog.findStationByMediaId(mediaId)
            ?: return Futures.immediateFuture(SessionResult(SessionError.ERROR_BAD_VALUE))

        scope.launch {
            addToOrRemoveFromFavoritesUseCase.execute(station)
            catalog.setFavorites(getFavoriteRadioStationsUseCase.execute().firstOrNull().orEmpty())
            sessionProvider()?.notifyChildrenChanged(
                PlaybackContract.FAVORITES,
                catalog.favoriteStations.size,
                null
            )
            sessionProvider()?.notifyChildrenChanged(
                PlaybackContract.ROOT_ID,
                catalog.rootChildrenCount(),
                null
            )
            sessionProvider()?.setMediaButtonPreferences(
                listOf(
                    FavoriteButtonProvider.create(
                        context = context,
                        currentMediaItem = player.currentMediaItem,
                        favoriteStations = catalog.favoriteStations
                    )
                )
            )
        }

        return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
    }

    override fun onSubscribe(
        session: MediaLibraryService.MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        parentId: String,
        params: MediaLibraryService.LibraryParams?
    ): ListenableFuture<LibraryResult<Void>> {
        if (catalog.shouldRefreshFavorites(parentId)) {
            scope.launch {
                catalog.setFavorites(getFavoriteRadioStationsUseCase.execute().firstOrNull().orEmpty())
                sessionProvider()?.notifyChildrenChanged(parentId, catalog.childCount(parentId), params)
            }
        }
        return Futures.immediateFuture(LibraryResult.ofVoid())
    }

    override fun onGetLibraryRoot(
        session: MediaLibraryService.MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        params: MediaLibraryService.LibraryParams?
    ): ListenableFuture<LibraryResult<MediaItem>> {
        val rootItem = MediaItem.Builder()
            .setMediaId(PlaybackContract.ROOT_ID)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle("")
                    .setIsPlayable(false)
                    .setIsBrowsable(true)
                    .setMediaType(MediaMetadata.MEDIA_TYPE_FOLDER_RADIO_STATIONS)
                    .build()
            )
            .build()
        return Futures.immediateFuture(LibraryResult.ofItem(rootItem, params))
    }

    override fun onGetChildren(
        session: MediaLibraryService.MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        parentId: String,
        page: Int,
        pageSize: Int,
        params: MediaLibraryService.LibraryParams?
    ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
        if (catalog.shouldRefreshFavorites(parentId)) {
            scope.launch {
                catalog.setFavorites(getFavoriteRadioStationsUseCase.execute().firstOrNull().orEmpty())
            }
        }

        val children = catalog.childrenForParent(context, parentId)
        return Futures.immediateFuture(
            LibraryResult.ofItemList(ImmutableList.copyOf(children), params)
        )
    }

    override fun onGetItem(
        session: MediaLibraryService.MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        mediaId: String
    ): ListenableFuture<LibraryResult<MediaItem>> {
        val station = catalog.findStationByMediaId(mediaId)
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
        val selectedList = catalog.resolveListByRequestedMediaId(requestedMediaId)
            ?: return Futures.immediateFuture(mediaItems)

        val stationId = requestedMediaId.substringAfter("_")
        val selectedIndex = selectedList.indexOfFirst { it.id == stationId }
        if (selectedIndex == -1) {
            return Futures.immediateFuture(mutableListOf())
        }

        val reorderedStations = selectedList.subList(selectedIndex, selectedList.size) +
                selectedList.subList(0, selectedIndex)
        player.repeatMode = Player.REPEAT_MODE_ALL
        val prefix = catalog.prefixForList(selectedList)
        val resultItems = reorderedStations.map { it.toMediaItem(prefix) }.toMutableList()
        return Futures.immediateFuture(resultItems)
    }
}
