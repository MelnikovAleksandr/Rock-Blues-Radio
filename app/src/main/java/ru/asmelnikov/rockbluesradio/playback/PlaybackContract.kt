package ru.asmelnikov.rockbluesradio.playback

object PlaybackContract {
    const val ROOT_ID = "root"
    const val ROCK_STATIONS = "rock"
    const val BLUES_STATIONS = "blues"
    const val FAVORITES = "favorites"
    const val COMMAND_TOGGLE_FAVORITE = "command_toggle_favorite"

    val stationFolders = listOf(ROCK_STATIONS, BLUES_STATIONS, FAVORITES)

    private const val MEDIA_ID_SEPARATOR = "_"

    fun mediaId(folderId: String, stationId: String): String = "$folderId$MEDIA_ID_SEPARATOR$stationId"

    fun stationId(mediaId: String): String = mediaId.substringAfter(MEDIA_ID_SEPARATOR)

    fun folderId(mediaId: String): String? =
        stationFolders.firstOrNull { mediaId.startsWith("$it$MEDIA_ID_SEPARATOR") }
}
