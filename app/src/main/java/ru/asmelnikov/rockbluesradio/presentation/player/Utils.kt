package ru.asmelnikov.rockbluesradio.presentation.player

import androidx.media3.common.MediaItem
import androidx.media3.common.Player

fun Player.updatePlaylist(incoming: List<MediaItem>) {
    val currentCount = mediaItemCount
    if (currentCount == incoming.size) {
        var isSame = true
        for (i in 0 until currentCount) {
            if (getMediaItemAt(i).mediaId != incoming[i].mediaId) {
                isSame = false
                break
            }
        }
        if (isSame) return
    }
    val currentMediaId = if (currentMediaItem != null) {
        currentMediaItem!!.mediaId
    } else null
    setMediaItems(incoming)
    if (currentMediaId != null) {
        val newIndex = incoming.indexOfFirst { it.mediaId == currentMediaId }
        if (newIndex != -1) {
            seekTo(newIndex, 0)
        }
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

            Player.STATE_IDLE -> {}
        }
    }

    seekToDefaultPosition(index)
    playWhenReady = true
    prepare()
}

val PlayerState.isBuffering get() = playbackState == Player.STATE_BUFFERING