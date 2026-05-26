package ru.asmelnikov.rockbluesradio.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import ru.asmelnikov.rockbluesradio.presentation.player.PlayerState
import ru.asmelnikov.rockbluesradio.presentation.player.isBuffering

@Composable
fun CompactPlayerView(
    modifier: Modifier = Modifier,
    playerState: PlayerState
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val currentMediaItem = playerState.currentMediaItem
                if (currentMediaItem != null) {
                    MiniPlayerArtworkView(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .padding(4.dp),
                        artworkUri = currentMediaItem.mediaMetadata.artworkUri
                    )
                    Text(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .weight(1f),
                        text = currentMediaItem.mediaMetadata.displayTitle.toString(),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            PlayPauseButton(
                modifier = Modifier
                    .padding(end = 24.dp)
                    .size(40.dp),
                isPlaying = playerState.isPlaying,
                isBuffering = playerState.isBuffering,
                iconTint = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                if (playerState.player.playbackState == Player.STATE_IDLE && playerState.currentMediaItem != null) {
                    playerState.player.prepare()
                    playerState.player.play()
                } else {
                    playerState.player.playWhenReady = !playerState.player.playWhenReady
                }
            }
        }
    }
}