package ru.asmelnikov.rockbluesradio.presentation.components.player.expanded

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ru.asmelnikov.rockbluesradio.presentation.components.player.PlayPauseButton
import ru.asmelnikov.rockbluesradio.presentation.components.player.SkipButton
import ru.asmelnikov.rockbluesradio.presentation.components.player.SkipDirection
import ru.asmelnikov.rockbluesradio.presentation.theme.dimens

@Composable
fun PlayerControls(
    isPlaying: Boolean,
    isBuffering: Boolean,
    onPlayPauseClick: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val skipButtonModifier = Modifier
        .size(dimens.large)
        .padding(dimens.small1)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SkipButton(
            modifier = skipButtonModifier,
            direction = SkipDirection.Previous,
            onClick = onPrevious,
        )
        PlayPauseButton(
            modifier = Modifier
                .size(dimens.large)
                .background(color = MaterialTheme.colorScheme.onSurface, shape = CircleShape)
                .padding(dimens.small1),
            isPlaying = isPlaying,
            isBuffering = isBuffering,
            iconTint = MaterialTheme.colorScheme.surface,
            onClick = onPlayPauseClick,
        )
        SkipButton(
            modifier = skipButtonModifier,
            direction = SkipDirection.Next,
            onClick = onNext,
        )
    }
}
