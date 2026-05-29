package ru.asmelnikov.rockbluesradio.presentation.components.expanded

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
import ru.asmelnikov.rockbluesradio.presentation.components.NextButton
import ru.asmelnikov.rockbluesradio.presentation.components.PlayPauseButton
import ru.asmelnikov.rockbluesradio.presentation.components.PreviousButton
import ru.asmelnikov.rockbluesradio.presentation.theme.dimens

@Composable
fun PlayerControls(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    isBuffering: Boolean,
    onPlayPauseClick: () -> Unit,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        PreviousButton(
            modifier = Modifier
                .size(dimens.large)
                .padding(dimens.small1),
            iconTint = MaterialTheme.colorScheme.onSurface
        ) {
            onPrevClick()
        }
        PlayPauseButton(
            modifier = Modifier
                .size(dimens.large)
                .background(color = MaterialTheme.colorScheme.onSurface, shape = CircleShape)
                .padding(dimens.small1),
            isPlaying = isPlaying,
            isBuffering = isBuffering,
            iconTint = MaterialTheme.colorScheme.surface,
            onTogglePlayPause = onPlayPauseClick
        )
        NextButton(
            modifier = Modifier
                .size(dimens.large)
                .padding(dimens.small1),
            iconTint = MaterialTheme.colorScheme.onSurface
        ) {
            onNextClick()
        }
    }
}