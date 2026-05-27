package ru.asmelnikov.rockbluesradio.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.rememberHazeState
import ru.asmelnikov.rockbluesradio.presentation.theme.RockBluesRadioTheme
import ru.asmelnikov.rockbluesradio.presentation.theme.dimens

@Composable
fun CompactPlayerView(
    modifier: Modifier = Modifier,
    currentMediaItem: MediaItem?,
    isPlaying: Boolean,
    isBuffering: Boolean,
    hazeState: HazeState,
    onPlayPauseClick: () -> Unit
) {

    Card(
        modifier = modifier
            .height(dimens.regular)
            .clip(MaterialTheme.shapes.medium),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .hazeEffect(state = hazeState)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = dimens.small1),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentMediaItem != null) {
                    MiniPlayerArtworkView(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .padding(dimens.extraSmall1),
                        artworkUri = currentMediaItem.mediaMetadata.artworkUri
                    )
                    Text(
                        modifier = Modifier
                            .padding(start = dimens.small1)
                            .weight(1f),
                        text = currentMediaItem.mediaMetadata.displayTitle.toString(),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            PlayPauseButton(
                modifier = Modifier
                    .padding(end = dimens.medium2)
                    .size(dimens.medium4),
                isPlaying = isPlaying,
                isBuffering = isBuffering,
                iconTint = MaterialTheme.colorScheme.onPrimaryContainer,
                onTogglePlayPause = onPlayPauseClick
            )
        }
    }
}

@Preview
@Preview(device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun CompactPlayerViewPreview1(modifier: Modifier = Modifier) {
    RockBluesRadioTheme(darkTheme = true) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            CompactPlayerView(
                modifier = Modifier.padding(12.dp),
                currentMediaItem = mockMediaItem(),
                isPlaying = false,
                isBuffering = false,
                hazeState = rememberHazeState(),
                onPlayPauseClick = {}
            )
        }
    }
}

@Preview
@Composable
fun CompactPlayerViewPreview2(modifier: Modifier = Modifier) {
    RockBluesRadioTheme {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            CompactPlayerView(
                modifier = Modifier.padding(12.dp),
                currentMediaItem = mockMediaItem(),
                isPlaying = true,
                isBuffering = false,
                hazeState = rememberHazeState(),
                onPlayPauseClick = {}
            )
        }
    }
}

@Preview
@Composable
fun CompactPlayerViewPreview3(modifier: Modifier = Modifier) {
    RockBluesRadioTheme(darkTheme = true) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            CompactPlayerView(
                modifier = Modifier.padding(12.dp),
                currentMediaItem = mockMediaItem(),
                isPlaying = false,
                isBuffering = true,
                hazeState = rememberHazeState(),
                onPlayPauseClick = {}
            )
        }
    }
}