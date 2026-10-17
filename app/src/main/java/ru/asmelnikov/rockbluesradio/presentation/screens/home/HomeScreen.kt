package ru.asmelnikov.rockbluesradio.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import ru.asmelnikov.rockbluesradio.domain.model.Genre
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation
import ru.asmelnikov.rockbluesradio.presentation.components.station.StationList
import ru.asmelnikov.rockbluesradio.presentation.components.topbar.GenreSwitcher
import ru.asmelnikov.rockbluesradio.presentation.components.topbar.HomeTopBar
import ru.asmelnikov.rockbluesradio.presentation.player.NowPlaying
import ru.asmelnikov.rockbluesradio.presentation.preview.PreviewData
import ru.asmelnikov.rockbluesradio.presentation.theme.RockBluesRadioTheme

@Composable
fun HomeScreen(
    nowPlaying: NowPlaying,
    hazeState: HazeState,
    onStationClick: (stations: List<RadioStation>, index: Int) -> Unit,
    onFavoritesClick: () -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    HomeContent(
        state = state,
        nowPlaying = nowPlaying,
        hazeState = hazeState,
        onStationClick = onStationClick,
        onFavoritesClick = onFavoritesClick,
        onFavoriteClick = viewModel::onFavoriteClick,
    )
}

@Composable
fun HomeContent(
    state: HomeUiState,
    nowPlaying: NowPlaying,
    hazeState: HazeState,
    onStationClick: (stations: List<RadioStation>, index: Int) -> Unit,
    onFavoritesClick: () -> Unit,
    onFavoriteClick: (RadioStation) -> Unit,
    modifier: Modifier = Modifier,
) {
    val genres = Genre.entries
    val pagerState = rememberPagerState { genres.size }
    val scope = rememberCoroutineScope()
    val pagerProgress by remember(pagerState) {
        derivedStateOf { pagerState.currentPage + pagerState.currentPageOffsetFraction }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column {
                HomeTopBar(
                    showFavoritesButton = state.showFavoritesButton,
                    hazeState = hazeState,
                    onFavoritesClick = onFavoritesClick,
                )
                GenreSwitcher(
                    genres = genres,
                    pagerProgress = pagerProgress,
                    hazeState = hazeState,
                    onGenreClick = { genre -> scope.launch { pagerState.animateScrollToPage(genre.ordinal) } },
                )
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .hazeSource(state = hazeState)
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize(),
        ) {
            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = pagerState,
            ) { page ->
                val stations = state.stationsOf(genres[page])
                StationList(
                    modifier = Modifier.fillMaxSize(),
                    stations = stations,
                    nowPlaying = nowPlaying,
                    hazeState = hazeState,
                    contentPadding = PaddingValues(
                        top = innerPadding.calculateTopPadding(),
                        bottom = innerPadding.calculateBottomPadding(),
                    ),
                    onStationClick = { index -> onStationClick(stations, index) },
                    onFavoriteClick = onFavoriteClick,
                )
            }
        }
    }
}

@Preview(locale = "ru")
@Preview(locale = "ru", device = "spec:parent=pixel_5,orientation=landscape")
@Preview(locale = "ru", device = "id:automotive_1408p_landscape_with_play")
@Composable
private fun HomeContentPreview() {
    RockBluesRadioTheme(darkTheme = true) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background),
        ) {
            HomeContent(
                state = HomeUiState(
                    isLoading = false,
                    stationsByGenre = Genre.entries.associateWith { PreviewData.stations() },
                    showFavoritesButton = true,
                ),
                nowPlaying = NowPlaying(),
                hazeState = rememberHazeState(),
                onStationClick = { _, _ -> },
                onFavoritesClick = {},
                onFavoriteClick = {},
            )
        }
    }
}
