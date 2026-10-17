package ru.asmelnikov.rockbluesradio.presentation.components.player.expanded

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.DragInteraction
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
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.media3.common.MediaItem
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import ru.asmelnikov.rockbluesradio.presentation.components.common.StationArtwork
import ru.asmelnikov.rockbluesradio.presentation.player.PlayerState
import ru.asmelnikov.rockbluesradio.presentation.preview.PreviewData
import ru.asmelnikov.rockbluesradio.presentation.theme.RockBluesRadioTheme
import ru.asmelnikov.rockbluesradio.presentation.theme.dimens
import ru.asmelnikov.rockbluesradio.presentation.utils.isPortrait

private const val ARTWORK_SIZE_FRACTION = 0.5f
private const val INACTIVE_ARTWORK_SCALE = 0.75f

@Composable
fun ExpandedPlayer(
    playerState: PlayerState,
    onCollapse: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val mediaItems = playerState.mediaItems
    val currentIndex = playerState.mediaItemIndex
    val pagerState = rememberPagerState(initialPage = currentIndex) { mediaItems.size }

    PagerFollowsPlayerEffect(pagerState = pagerState, playerIndex = currentIndex, pageCount = mediaItems.size)
    PlayerFollowsUserSwipeEffect(pagerState = pagerState, onPageSelected = playerState::skipTo)

    ExpandedPlayerContent(
        modifier = modifier,
        pagerState = pagerState,
        mediaItems = mediaItems,
        title = playerState.currentMediaItem?.mediaMetadata?.displayTitle?.toString().orEmpty(),
        isPlaying = playerState.isPlaying,
        isBuffering = playerState.isBuffering,
        onPlayPauseClick = playerState::togglePlayPause,
        onCollapse = onCollapse,
        onPrevious = playerState::skipToPrevious,
        onNext = playerState::skipToNext,
    )
}

@Composable
private fun PagerFollowsPlayerEffect(pagerState: PagerState, playerIndex: Int, pageCount: Int) {
    LaunchedEffect(playerIndex, pageCount) {
        if (playerIndex !in 0 until pageCount || pagerState.currentPage == playerIndex) return@LaunchedEffect
        if (abs(pagerState.currentPage - playerIndex) > 1) {
            pagerState.scrollToPage(playerIndex)
        } else {
            pagerState.animateScrollToPage(playerIndex)
        }
    }
}

@Composable
private fun PlayerFollowsUserSwipeEffect(pagerState: PagerState, onPageSelected: (Int) -> Unit) {
    val currentOnPageSelected by rememberUpdatedState(onPageSelected)
    LaunchedEffect(pagerState) {
        var isScrolledByUser = false
        launch {
            pagerState.interactionSource.interactions.collect { interaction ->
                if (interaction is DragInteraction.Start) isScrolledByUser = true
            }
        }
        snapshotFlow { pagerState.isScrollInProgress }
            .filter { inProgress -> !inProgress }
            .collect {
                if (isScrolledByUser) {
                    isScrolledByUser = false
                    currentOnPageSelected(pagerState.currentPage)
                }
            }
    }
}

@Composable
private fun ExpandedPlayerContent(
    pagerState: PagerState,
    mediaItems: List<MediaItem>,
    title: String,
    isPlaying: Boolean,
    isBuffering: Boolean,
    onPlayPauseClick: () -> Unit,
    onCollapse: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val controls: @Composable (Modifier) -> Unit = { controlsModifier ->
        PlayerControls(
            modifier = controlsModifier,
            isPlaying = isPlaying,
            isBuffering = isBuffering,
            onPlayPauseClick = onPlayPauseClick,
            onPrevious = onPrevious,
            onNext = onNext,
        )
    }

    if (isPortrait()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.statusBarsPadding())
            CollapseBar(onCollapse = onCollapse)
            Spacer(modifier = Modifier.height(dimens.medium2))
            if (mediaItems.isNotEmpty()) {
                ArtworkPager(
                    modifier = Modifier.fillMaxWidth(),
                    pagerState = pagerState,
                    mediaItems = mediaItems,
                    orientation = Orientation.Horizontal,
                )
            }
            Spacer(modifier = Modifier.height(dimens.medium2))
            PlayerTitle(title = title)
            Spacer(modifier = Modifier.height(dimens.medium2))
            controls(Modifier)
            Spacer(modifier = Modifier.height(dimens.medium2))
        }
    } else if (mediaItems.isNotEmpty()) {
        Box(modifier = modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                ArtworkPager(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    pagerState = pagerState,
                    mediaItems = mediaItems,
                    orientation = Orientation.Vertical,
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Spacer(modifier = Modifier.height(dimens.medium2))
                    PlayerTitle(title = title)
                    Spacer(modifier = Modifier.height(dimens.medium2))
                    controls(Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(dimens.medium2))
                }
            }
            Row(modifier = Modifier.align(Alignment.TopStart)) {
                Spacer(modifier = Modifier.statusBarsPadding())
                CollapseBar(onCollapse = onCollapse)
            }
        }
    }
}

@Composable
private fun ArtworkPager(
    pagerState: PagerState,
    mediaItems: List<MediaItem>,
    orientation: Orientation,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier) {
        val mainAxisSize = if (orientation == Orientation.Vertical) maxHeight else maxWidth
        val artworkSize = mainAxisSize * ARTWORK_SIZE_FRACTION
        val sidePadding = ((mainAxisSize - artworkSize) / 2).coerceAtLeast(0.dp)

        val pageContent: @Composable PagerScope.(Int) -> Unit = { page ->
            StationArtwork(
                modifier = Modifier
                    .size(artworkSize)
                    .graphicsLayer {
                        val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                        val scale = lerp(
                            start = INACTIVE_ARTWORK_SCALE,
                            stop = 1f,
                            fraction = 1f - pageOffset.absoluteValue.coerceIn(0f, 1f),
                        )
                        scaleX = scale
                        scaleY = scale
                    },
                artworkUrl = mediaItems[page].mediaMetadata.artworkUri?.toString(),
            )
        }

        when (orientation) {
            Orientation.Horizontal -> HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = sidePadding),
                beyondViewportPageCount = 1,
                pageContent = pageContent,
            )

            Orientation.Vertical -> VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = sidePadding),
                beyondViewportPageCount = 1,
                pageContent = pageContent,
            )
        }
    }
}

@Preview(device = "id:pixel_tablet")
@Preview(device = "id:pixel_xl")
@Composable
private fun ExpandedPlayerContentPreview() {
    val mediaItems = PreviewData.mediaItems()
    RockBluesRadioTheme(darkTheme = true) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainerLowest),
        ) {
            ExpandedPlayerContent(
                pagerState = rememberPagerState(initialPage = 2) { mediaItems.size },
                mediaItems = mediaItems,
                title = "Test title",
                isPlaying = true,
                isBuffering = false,
                onPlayPauseClick = {},
                onCollapse = {},
                onPrevious = {},
                onNext = {},
            )
        }
    }
}
