package ru.asmelnikov.rockbluesradio.playback

import androidx.media3.common.MediaItem
import androidx.media3.common.Player

fun Player.hasSameMediaIds(items: List<MediaItem>): Boolean =
    mediaItemCount == items.size && items.indices.all { getMediaItemAt(it).mediaId == items[it].mediaId }

fun Player.replaceQueueAroundCurrentItem(items: List<MediaItem>) {
    val currentMediaId = currentMediaItem?.mediaId ?: return
    if (hasSameMediaIds(items)) return

    val currentIndex = currentMediaItemIndex
    val newIndex = items.indexOfFirst { it.mediaId == currentMediaId }
    val itemsBefore = if (newIndex == -1) emptyList() else items.subList(0, newIndex)
    val itemsAfter = if (newIndex == -1) items else items.subList(newIndex + 1, items.size)

    replaceMediaItems(currentIndex + 1, mediaItemCount, itemsAfter)
    replaceMediaItems(0, currentIndex, itemsBefore)
}
