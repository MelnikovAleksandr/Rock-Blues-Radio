package ru.asmelnikov.rockbluesradio.presentation.service

import android.content.Context
import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.session.CommandButton
import androidx.media3.session.SessionCommand
import ru.asmelnikov.rockbluesradio.R
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation

object FavoriteButtonProvider {
    fun create(
        context: Context,
        currentMediaItem: MediaItem?,
        favoriteStations: List<RadioStation>
    ): CommandButton {
        val isFavorite = currentMediaItem?.mediaId?.let { mediaId ->
            val stationId = mediaId.substringAfter("_")
            favoriteStations.any { it.id == stationId }
        } == true

        val icon = if (isFavorite) {
            CommandButton.ICON_HEART_FILLED
        } else {
            CommandButton.ICON_HEART_UNFILLED
        }

        return CommandButton.Builder(icon)
            .setSessionCommand(
                SessionCommand(
                    PlaybackContract.COMMAND_TOGGLE_FAVORITE,
                    Bundle.EMPTY
                )
            )
            .setDisplayName(context.getString(R.string.add_remove_favorite))
            .build()
    }
}
