package ru.asmelnikov.rockbluesradio.presentation.player

import androidx.media3.common.MediaItem
import androidx.media3.common.Player

val Player.mediaItems: List<MediaItem>
    get() = List(mediaItemCount, ::getMediaItemAt)

fun Player.updatePlaylist(items: List<MediaItem>) {
    if (hasSameMediaIds(items)) return

    val currentMediaId = currentMediaItem?.mediaId
    setMediaItems(items)
    val newIndex = items.indexOfFirst { it.mediaId == currentMediaId }
    if (currentMediaId != null && newIndex != -1) {
        seekTo(newIndex, 0)
    }
}

fun Player.playMediaAt(index: Int) {
    if (currentMediaItemIndex == index) {
        when (playbackState) {
            Player.STATE_READY, Player.STATE_BUFFERING -> {
                playWhenReady = !playWhenReady
                return
            }

            Player.STATE_ENDED -> {
                seekToDefaultPosition(index)
                playWhenReady = true
                prepare()
                return
            }

            Player.STATE_IDLE -> Unit
        }
    }

    seekToDefaultPosition(index)
    playWhenReady = true
    prepare()
}

fun Player.togglePlayPause() {
    if (playbackState == Player.STATE_IDLE && currentMediaItem != null) {
        prepare()
        play()
    } else {
        playWhenReady = !playWhenReady
    }
}

private fun Player.hasSameMediaIds(items: List<MediaItem>): Boolean =
    mediaItemCount == items.size && items.indices.all { getMediaItemAt(it).mediaId == items[it].mediaId }
