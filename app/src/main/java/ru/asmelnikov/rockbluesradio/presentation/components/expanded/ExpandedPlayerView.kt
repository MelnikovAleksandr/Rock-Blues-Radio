package ru.asmelnikov.rockbluesradio.presentation.components.expanded

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.media3.common.MediaItem
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import ru.asmelnikov.rockbluesradio.presentation.components.mockMediaItems
import ru.asmelnikov.rockbluesradio.presentation.player.PlayerState
import ru.asmelnikov.rockbluesradio.presentation.player.isBuffering
import ru.asmelnikov.rockbluesradio.presentation.theme.RockBluesRadioTheme
import ru.asmelnikov.rockbluesradio.presentation.theme.dimens
import ru.asmelnikov.rockbluesradio.presentation.utils.isPortrait
import kotlin.math.absoluteValue

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

    ExpandedContent(
        modifier = modifier,
        pagerState = pagerState,
        title = playerState.currentMediaItem?.mediaMetadata?.displayTitle.toString(),
        mediaItems = mediaItems,
        isPlaying = playerState.isPlaying,
        isBuffering = playerState.isBuffering,
        onPlayPauseClick = {
            with(playerState.player) {
                playWhenReady = !playWhenReady
            }
        },
        onCollapseTap = onCollapseTap,
        onPrevClick = onPrevClick,
        onNextClick = onNextClick,
    )

}

@Composable
fun ExpandedContent(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    mediaItems: List<MediaItem>,
    title: String,
    isPlaying: Boolean,
    isBuffering: Boolean,
    onPlayPauseClick: () -> Unit,
    onCollapseTap: () -> Unit,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit
) {

    if (isPortrait()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.statusBarsPadding())
            TopBar(onCollapseTap = onCollapseTap)
            Spacer(modifier = Modifier.height(dimens.medium2))
            if (mediaItems.isNotEmpty()) {
                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    val artworkSize = maxWidth * 0.5f
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = (maxWidth - artworkSize) / 2),
                        beyondViewportPageCount = 1,
                    ) { page ->
                        PlayerArtwork(
                            modifier = Modifier
                                .size(artworkSize)
                                .graphicsLayer {
                                    val pageOffset =
                                        (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                                    val scale = lerp(
                                        start = 0.75f,
                                        stop = 1f,
                                        fraction = 1f - pageOffset.absoluteValue.coerceIn(0f, 1f)
                                    )
                                    scaleX = scale
                                    scaleY = scale
                                },
                            artworkUri = mediaItems[page].mediaMetadata.artworkUri ?: Uri.EMPTY
                        )
                    }
                }

            }
            Spacer(modifier = Modifier.height(dimens.medium2))
            PlayerTitle(title = title)
            Spacer(modifier = Modifier.height(dimens.medium2))
            PlayerControls(
                isBuffering = isBuffering,
                isPlaying = isPlaying,
                onPlayPauseClick = onPlayPauseClick,
                onPrevClick = onPrevClick,
                onNextClick = onNextClick
            )
            Spacer(modifier = Modifier.height(dimens.medium2))
        }
    } else {
        if (mediaItems.isNotEmpty()) {
            Box(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    BoxWithConstraints(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        val artworkSize = maxHeight * 0.5f

                        VerticalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                vertical = ((maxHeight - artworkSize) / 2).coerceAtLeast(0.dp)
                            ),

                            beyondViewportPageCount = 1
                        ) { page ->
                            PlayerArtwork(
                                modifier = Modifier
                                    .size(artworkSize)
                                    .graphicsLayer {
                                        val pageOffset =
                                            (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                                        val scale = lerp(
                                            start = 0.75f,
                                            stop = 1f,
                                            fraction = 1f - pageOffset.absoluteValue.coerceIn(
                                                0f,
                                                1f
                                            )
                                        )
                                        scaleX = scale
                                        scaleY = scale
                                    },
                                artworkUri = mediaItems[page].mediaMetadata.artworkUri ?: Uri.EMPTY
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Spacer(modifier = Modifier.height(dimens.medium2))
                        PlayerTitle(title = title)
                        Spacer(modifier = Modifier.height(dimens.medium2))
                        PlayerControls(
                            modifier = Modifier.fillMaxWidth(),
                            isBuffering = isBuffering,
                            isPlaying = isPlaying,
                            onPlayPauseClick = onPlayPauseClick,
                            onPrevClick = onPrevClick,
                            onNextClick = onNextClick
                        )
                        Spacer(modifier = Modifier.height(dimens.medium2))
                    }
                }
                Row(
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Spacer(modifier = Modifier.statusBarsPadding())
                    TopBar(onCollapseTap = onCollapseTap)
                }
            }
        }
    }
}

@Preview(device = "id:pixel_tablet")
@Preview(device = "id:pixel_xl")
@Composable
fun ExpandedContentPreview1(modifier: Modifier = Modifier) {
    RockBluesRadioTheme(darkTheme = true) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
        ) {
            ExpandedContent(
                pagerState = rememberPagerState(initialPage = 2) {
                    mockMediaItems().size
                },
                mediaItems = mockMediaItems(),
                title = "Test title",
                isPlaying = true,
                isBuffering = false,
                onPlayPauseClick = {},
                onCollapseTap = {},
                onPrevClick = {},
                onNextClick = {}
            )
        }
    }
}
