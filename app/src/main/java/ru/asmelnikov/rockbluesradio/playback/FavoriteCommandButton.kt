package ru.asmelnikov.rockbluesradio.playback

import android.content.Context
import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.session.CommandButton
import androidx.media3.session.SessionCommand
import ru.asmelnikov.rockbluesradio.R
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation

object FavoriteCommandButton {

    val sessionCommand = SessionCommand(PlaybackContract.COMMAND_TOGGLE_FAVORITE, Bundle.EMPTY)

    fun preferences(
        context: Context,
        currentMediaItem: MediaItem?,
        favoriteStations: List<RadioStation>,
    ): List<CommandButton> = listOf(create(context, currentMediaItem, favoriteStations))

    private fun create(
        context: Context,
        currentMediaItem: MediaItem?,
        favoriteStations: List<RadioStation>,
    ): CommandButton {
        val currentStationId = currentMediaItem?.mediaId?.let(PlaybackContract::stationId)
        val isFavorite = currentStationId != null && favoriteStations.any { it.id == currentStationId }
        val icon = if (isFavorite) CommandButton.ICON_HEART_FILLED else CommandButton.ICON_HEART_UNFILLED

        return CommandButton.Builder(icon)
            .setSessionCommand(sessionCommand)
            .setDisplayName(context.getString(R.string.add_remove_favorite))
            .build()
    }
}
