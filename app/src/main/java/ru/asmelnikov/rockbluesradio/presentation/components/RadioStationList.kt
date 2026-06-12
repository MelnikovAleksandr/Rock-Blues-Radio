package ru.asmelnikov.rockbluesradio.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation
import ru.asmelnikov.rockbluesradio.presentation.screens.main.ScreenState
import ru.asmelnikov.rockbluesradio.presentation.theme.RockBluesRadioTheme

@Composable
fun RadioStationList(
    modifier: Modifier = Modifier,
    state: ScreenState,
    currentPlayingStationId: String,
    hazeState: HazeState,
    isPlayerSetUp: Boolean,
    onItemClick: (List<RadioStation>, Int) -> Unit = { _, _ -> },
    onFavoritesClick: () -> Unit = {},
    onFavClick: (RadioStation) -> Unit = {}
) {
    val pagerState = rememberPagerState(initialPage = 0) { 2 }
    val scope = rememberCoroutineScope()
    val pagerProgress by remember(pagerState) {
        derivedStateOf { pagerState.currentPage + pagerState.currentPageOffsetFraction }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column {
                HomeTitleBar(
                    hazeState = hazeState,
                    favoritesEnabled = state.showFavoritesButton,
                    onFavClick = onFavoritesClick
                )
                GenrePagerSwitcher(
                    modifier = Modifier,
                    hazeState = hazeState,
                    pagerProgress = pagerProgress,
                    onRockClick = {
                        scope.launch { pagerState.animateScrollToPage(0) }
                    },
                    onBluesClick = {
                        scope.launch { pagerState.animateScrollToPage(1) }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .hazeSource(state = hazeState)
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
        ) {
            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = pagerState
            ) { page ->
                val items = remember(page, state.rockItems, state.bluesItems) {
                    if (page == 0) state.rockItems else state.bluesItems
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        top = innerPadding.calculateTopPadding(),
                        bottom = innerPadding.calculateBottomPadding()
                    )
                ) {
                    itemsIndexed(items = items, key = { _, item -> item.id }) { index, item ->
                        RadioStationRow(
                            modifier = Modifier
                                .hazeSource(state = hazeState)
                                .animateItem()
                                .clickable { onItemClick(items, index) }
                                .fillMaxWidth(),
                            item = item,
                            currentPlayingStationId = currentPlayingStationId,
                            isFavorite = item.isFavorite,
                            onFavClick = onFavClick
                        )
                    }

                    item { Spacer(modifier = Modifier.navigationBarsPadding()) }
                    if (isPlayerSetUp) {
                        item { Spacer(modifier = Modifier.height(60.dp)) }
                    }
                }
            }
        }
    }
}

@Preview(locale = "ru")
@Preview(locale = "ru", device = "spec:parent=pixel_5,orientation=landscape")
@Preview(locale = "ru", device = "id:automotive_1408p_landscape_with_play")
@Composable
fun RadioStationListPreview1(modifier: Modifier = Modifier) {
    RockBluesRadioTheme(darkTheme = true) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            RadioStationList(
                state = ScreenState(
                    rockItems = mockRadioStations(),
                    bluesItems = mockRadioStations(),
                    showFavoritesButton = true
                ),
                hazeState = rememberHazeState(),
                currentPlayingStationId = "",
                isPlayerSetUp = false,
                onItemClick = { _, _ -> },
                onFavoritesClick = {},
                onFavClick = {}
            )
        }
    }
}

@Preview(locale = "ru")
@Preview(locale = "ru", device = "spec:parent=pixel_5,orientation=landscape")
@Composable
fun RadioStationListPreview2(modifier: Modifier = Modifier) {
    RockBluesRadioTheme {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            RadioStationList(
                state = ScreenState(
                    rockItems = mockRadioStations(),
                    bluesItems = mockRadioStations(),
                    showFavoritesButton = false
                ),
                hazeState = rememberHazeState(),
                currentPlayingStationId = "",
                isPlayerSetUp = false,
                onItemClick = { _, _ -> },
                onFavoritesClick = {},
                onFavClick = {}
            )
        }
    }
}
