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
    setMediaItems(incoming)
}

fun Player.playMediaAt(index: Int) {
    if (currentMediaItemIndex == index && (playbackState == Player.STATE_READY || playbackState == Player.STATE_BUFFERING)) {
        if (!playWhenReady) {
            play()
        }
        return
    }
    if (currentMediaItemIndex == index && playbackState == Player.STATE_ENDED) {
        seekToDefaultPosition(index)
        playWhenReady = true
        prepare()
        return
    }
    seekToDefaultPosition(index)
    playWhenReady = true
    prepare()
}

val PlayerState.isBuffering get() = playbackState == Player.STATE_BUFFERING