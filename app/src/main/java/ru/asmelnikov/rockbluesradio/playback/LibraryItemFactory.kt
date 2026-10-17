package ru.asmelnikov.rockbluesradio.playback

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import ru.asmelnikov.rockbluesradio.R

class LibraryItemFactory(private val context: Context) {

    fun root(): MediaItem = browsableItem(mediaId = PlaybackContract.ROOT_ID, title = "")

    fun children(catalog: PlaybackCatalog, parentId: String): List<MediaItem> = when (parentId) {
        PlaybackContract.ROOT_ID -> catalog.rootFolders().map(::folder)
        else -> StationFolder.fromFolderId(parentId)?.let { catalog.stations(it).toMediaItems(it) }.orEmpty()
    }

    fun station(folderStation: FolderStation): MediaItem =
        folderStation.station.toMediaItem(folderStation.folder)

    fun queue(queue: StationQueue): List<MediaItem> = queue.stations.toMediaItems(queue.folder)

    private fun folder(folder: StationFolder): MediaItem = browsableItem(
        mediaId = folder.mediaId,
        title = context.getString(folder.titleRes),
        artworkUri = "android.resource://${context.packageName}/${folder.iconRes}",
    )

    private fun browsableItem(mediaId: String, title: String, artworkUri: String? = null): MediaItem {
        val metadata = MediaMetadata.Builder()
            .setTitle(title)
            .setIsPlayable(false)
            .setIsBrowsable(true)
            .setMediaType(MediaMetadata.MEDIA_TYPE_FOLDER_RADIO_STATIONS)
            .setArtworkUri(artworkUri?.toUri())
            .build()
        return MediaItem.Builder()
            .setMediaId(mediaId)
            .setMediaMetadata(metadata)
            .build()
    }

    @get:StringRes
    private val StationFolder.titleRes: Int
        get() = when (this) {
            StationFolder.Rock -> R.string.rock
            StationFolder.Blues -> R.string.blues
            StationFolder.Favorites -> R.string.favorite
        }

    @get:DrawableRes
    private val StationFolder.iconRes: Int
        get() = when (this) {
            StationFolder.Rock -> R.drawable.guitar_auto
            StationFolder.Blues -> R.drawable.sax_auto
            StationFolder.Favorites -> R.drawable.favourites_auto
        }
}
