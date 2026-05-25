package ru.asmelnikov.rockbluesradio.presentation.player

import androidx.media3.common.MediaItem
import androidx.media3.common.Player

fun Player.updatePlaylist(incoming: List<MediaItem>) {
    setMediaItems(incoming)
}

fun Player.playMediaAt(index: Int) {
    if (currentMediaItemIndex == index)
        return
    seekToDefaultPosition(index)
    playWhenReady = true
    prepare()
}

val PlayerState.isBuffering get() = playbackState == Player.STATE_BUFFERING