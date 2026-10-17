package ru.asmelnikov.rockbluesradio.presentation.player

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation
import ru.asmelnikov.rockbluesradio.playback.PlaybackContract
import ru.asmelnikov.rockbluesradio.playback.StationFolder
import ru.asmelnikov.rockbluesradio.playback.hasSameMediaIds
import ru.asmelnikov.rockbluesradio.playback.toMediaItems

@Stable
class PlayerState(private val player: Player) {

    var currentMediaItem: MediaItem? by mutableStateOf(player.currentMediaItem)
        private set

    var mediaItems: List<MediaItem> by mutableStateOf(player.currentMediaItems())
        private set

    var mediaItemIndex: Int by mutableIntStateOf(player.currentMediaItemIndex)
        private set

    @get:Player.State
    var playbackState: Int by mutableIntStateOf(player.playbackState)
        private set

    var isPlaying: Boolean by mutableStateOf(player.isPlaying)
        private set

    var playerError: PlaybackException? by mutableStateOf(player.playerError)
        private set

    val isBuffering: Boolean
        get() = playbackState == Player.STATE_BUFFERING

    val playingStationId: String?
        get() = currentMediaItem?.takeIf { isPlaying }?.mediaId?.let(PlaybackContract::stationId)

    private val listener = object : Player.Listener {
        override fun onTimelineChanged(timeline: Timeline, reason: Int) {
            mediaItems = player.currentMediaItems()
            mediaItemIndex = player.currentMediaItemIndex
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            currentMediaItem = mediaItem
            mediaItemIndex = player.currentMediaItemIndex
        }

        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int,
        ) {
            mediaItemIndex = player.currentMediaItemIndex
        }

        override fun onPlaybackStateChanged(@Player.State playbackState: Int) {
            this@PlayerState.playbackState = playbackState
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            this@PlayerState.isPlaying = isPlaying
        }

        override fun onPlayerErrorChanged(error: PlaybackException?) {
            playerError = error
        }
    }

    init {
        player.addListener(listener)
    }

    fun playStation(folder: StationFolder, stations: List<RadioStation>, index: Int) {
        val items = stations.toMediaItems(folder)
        val selectedItem = items.getOrNull(index) ?: return
        if (player.currentMediaItem?.mediaId == selectedItem.mediaId) {
            togglePlayPause()
            return
        }

        if (player.hasSameMediaIds(items)) {
            player.seekToDefaultPosition(index)
        } else {
            player.setMediaItems(items, index, C.TIME_UNSET)
        }
        player.prepare()
        player.play()
    }

    fun togglePlayPause() {
        when (player.playbackState) {
            Player.STATE_IDLE -> {
                player.prepare()
                player.play()
            }

            Player.STATE_ENDED -> {
                player.seekToDefaultPosition()
                player.play()
            }

            else -> player.playWhenReady = !player.playWhenReady
        }
    }

    fun skipToPrevious() {
        player.seekToPreviousMediaItem()
    }

    fun skipToNext() {
        player.seekToNextMediaItem()
    }

    fun skipTo(index: Int) {
        if (index in mediaItems.indices && index != player.currentMediaItemIndex) {
            player.seekToDefaultPosition(index)
        }
    }

    fun retry() {
        player.prepare()
    }

    fun dispose() {
        player.removeListener(listener)
    }

    private fun Player.currentMediaItems(): List<MediaItem> = List(mediaItemCount, ::getMediaItemAt)
}

@Composable
fun rememberPlayerState(player: Player?): PlayerState? {
    var playerState by remember { mutableStateOf<PlayerState?>(null) }
    DisposableEffect(player) {
        if (player != null) {
            playerState = PlayerState(player)
        }
        onDispose { playerState?.dispose() }
    }
    return playerState
}
