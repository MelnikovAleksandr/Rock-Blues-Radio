package ru.asmelnikov.rockbluesradio.presentation.screens.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import org.koin.androidx.compose.koinViewModel
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation
import ru.asmelnikov.rockbluesradio.presentation.components.station.StationList
import ru.asmelnikov.rockbluesradio.presentation.components.topbar.FavoritesTopBar
import ru.asmelnikov.rockbluesradio.presentation.player.NowPlaying
import ru.asmelnikov.rockbluesradio.presentation.preview.PreviewData
import ru.asmelnikov.rockbluesradio.presentation.theme.RockBluesRadioTheme

@Composable
fun FavoritesScreen(
    nowPlaying: NowPlaying,
    hazeState: HazeState,
    onStationClick: (stations: List<RadioStation>, index: Int) -> Unit,
    onPlaylistUpdate: (List<RadioStation>) -> Unit,
    onBack: () -> Unit,
    viewModel: FavoritesViewModel = koinViewModel(),
) {
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val currentOnPlaylistUpdate by rememberUpdatedState(onPlaylistUpdate)
    val currentOnBack by rememberUpdatedState(onBack)

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is FavoritesEvent.FavoritesChanged -> currentOnPlaylistUpdate(event.favorites)
                FavoritesEvent.NavigateBack -> currentOnBack()
            }
        }
    }

    FavoritesContent(
        favorites = favorites,
        nowPlaying = nowPlaying,
        hazeState = hazeState,
        onStationClick = { index -> onStationClick(favorites, index) },
        onFavoriteClick = viewModel::onFavoriteClick,
        onBack = onBack,
    )
}

@Composable
fun FavoritesContent(
    favorites: List<RadioStation>,
    nowPlaying: NowPlaying,
    hazeState: HazeState,
    onStationClick: (index: Int) -> Unit,
    onFavoriteClick: (RadioStation) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .hazeSource(state = hazeState)
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize(),
        )
        StationList(
            stations = favorites,
            nowPlaying = nowPlaying,
            hazeState = hazeState,
            onStationClick = onStationClick,
            onFavoriteClick = onFavoriteClick,
            stickyHeader = {
                FavoritesTopBar(hazeState = hazeState, onBack = onBack)
            },
        )
    }
}

@Preview(locale = "ru")
@Composable
private fun FavoritesContentPreview() {
    RockBluesRadioTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background),
        ) {
            FavoritesContent(
                favorites = PreviewData.stations().map { it.copy(isFavorite = true) },
                nowPlaying = NowPlaying(),
                hazeState = rememberHazeState(),
                onStationClick = {},
                onFavoriteClick = {},
                onBack = {},
            )
        }
    }
}
