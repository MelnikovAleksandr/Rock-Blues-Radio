package ru.asmelnikov.rockbluesradio.presentation.player

import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.Player


internal val Player.currentMediaItems: List<MediaItem> get() {
    return List(mediaItemCount, ::getMediaItemAt)
}

fun Player.updatePlaylist(incoming: List<MediaItem>) {
    val oldMediaIds = currentMediaItems.map { it.mediaId }.toSet()
    val itemsToAdd = incoming.filterNot { item -> item.mediaId in oldMediaIds }
    Log.d("PlayerExt", "updatePlaylist: itemsToAdd: $itemsToAdd")
    addMediaItems(itemsToAdd)
}

fun Player.playMediaAt(index: Int) {
    if (currentMediaItemIndex == index)
        return
    seekToDefaultPosition(index)
    playWhenReady = true
    prepare()
}

val PlayerState.isBuffering get() = playbackState == Player.STATE_BUFFERING