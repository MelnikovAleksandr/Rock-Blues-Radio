package ru.asmelnikov.rockbluesradio.playback

object PlaybackContract {
    const val ROOT_ID = "root"
    const val COMMAND_TOGGLE_FAVORITE = "command_toggle_favorite"

    private const val MEDIA_ID_SEPARATOR = "_"

    fun stationMediaId(folder: StationFolder, stationId: String): String =
        "${folder.mediaId}$MEDIA_ID_SEPARATOR$stationId"

    fun stationId(mediaId: String): String = mediaId.substringAfter(MEDIA_ID_SEPARATOR)

    fun folderOf(mediaId: String?): StationFolder? =
        mediaId?.let { StationFolder.fromFolderId(it.substringBefore(MEDIA_ID_SEPARATOR, missingDelimiterValue = "")) }
}
