package ru.asmelnikov.rockbluesradio.presentation.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import ru.asmelnikov.rockbluesradio.R
import ru.asmelnikov.rockbluesradio.presentation.player.PlayerState
import ru.asmelnikov.rockbluesradio.presentation.player.isBuffering
import kotlin.math.absoluteValue
import kotlin.random.Random

@Composable
fun ExpandedPlayerView(
    modifier: Modifier = Modifier,
    playerState: PlayerState,
    onCollapseTap: () -> Unit,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit
) {
    val mediaItems = remember(playerState.player.mediaItemCount) {
        val player = playerState.player
        if (player.mediaItemCount > 0) {
            (0 until player.mediaItemCount).map { player.getMediaItemAt(it) }
        } else {
            emptyList()
        }
    }

    val currentIndex = playerState.mediaItemIndex

    val pagerState = rememberPagerState(
        initialPage = currentIndex,
        pageCount = { mediaItems.size }
    )

    LaunchedEffect(currentIndex) {
        if (mediaItems.isNotEmpty() && currentIndex in 0 until mediaItems.size) {
            if (pagerState.currentPage != currentIndex) {
                pagerState.animateScrollToPage(currentIndex)
            }
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .drop(1)
            .collect { page ->
                val currentPlayerIndex = playerState.mediaItemIndex
                if (page != currentPlayerIndex && page in 0 until mediaItems.size) {
                    if (page > currentPlayerIndex) {
                        onNextClick()
                    } else {
                        onPrevClick()
                    }
                }
            }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopBar(onCollapseTap = onCollapseTap)

        if (mediaItems.isNotEmpty()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 64.dp),
                pageSpacing = 8.dp,
                beyondViewportPageCount = 1
            ) { page ->
                PlayerArtwork(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .graphicsLayer {
                            val pageOffset =
                                ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue
                            val scale = lerp(
                                start = 0.8f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )
                            scaleX = scale
                            scaleY = scale
                            alpha = lerp(
                                start = 0.5f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )
                        },
                    artworkUri = mediaItems[page].mediaMetadata.artworkUri ?: Uri.EMPTY
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        PlayerTitle(playerState = playerState)
        Spacer(modifier = Modifier.height(24.dp))
        PlayerControls(
            playerState = playerState,
            onPrevClick = onPrevClick,
            onNextClick = onNextClick
        )
    }
}


@Composable
private fun PlayerArtwork(
    modifier: Modifier = Modifier,
    artworkUri: Uri
) {
    val placeholderPainter = painterResource(id = R.drawable.radio)

    val backgroundColor = remember(artworkUri) {
        val seed = artworkUri.hashCode()
        val random = Random(seed)
        Color.hsl(
            hue = random.nextFloat() * 360f,
            saturation = 0.5f,
            lightness = 0.8f,
            alpha = 1f
        )
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = artworkUri,
            contentDescription = null,
            placeholder = placeholderPainter,
            error = placeholderPainter
        )
    }
}

@Composable
private fun TopBar(onCollapseTap: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onCollapseTap) {
            Icon(
                modifier = Modifier.size(40.dp),
                painter = painterResource(id = R.drawable.keyboard_arrow_down),
                contentDescription = "Collapse",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun PlayerTitle(playerState: PlayerState) {
    Text(
        modifier = Modifier,
        text = playerState.currentMediaItem?.mediaMetadata?.displayTitle.toString(),
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun PlayerControls(
    playerState: PlayerState,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        PreviousButton(
            modifier = Modifier
                .size(80.dp)
                .padding(8.dp),
            iconTint = MaterialTheme.colorScheme.onSurface
        ) {
            onPrevClick()
        }
        PlayPauseButton(
            modifier = Modifier
                .size(80.dp)
                .background(color = MaterialTheme.colorScheme.onSurface, shape = CircleShape)
                .padding(8.dp),
            isPlaying = playerState.isPlaying,
            isBuffering = playerState.isBuffering,
            iconTint = MaterialTheme.colorScheme.surface
        ) {
            with(playerState.player) {
                playWhenReady = !playWhenReady
            }
        }
        NextButton(
            modifier = Modifier
                .size(80.dp)
                .padding(8.dp),
            iconTint = MaterialTheme.colorScheme.onSurface
        ) {
            onNextClick()
        }
    }
}
