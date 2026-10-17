package ru.asmelnikov.rockbluesradio.presentation.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import dev.chrisbanes.haze.HazeState
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation
import ru.asmelnikov.rockbluesradio.presentation.player.NowPlaying
import ru.asmelnikov.rockbluesradio.presentation.screens.favorites.FavoritesScreen
import ru.asmelnikov.rockbluesradio.presentation.screens.home.HomeScreen

@Composable
fun NavGraph(
    backStack: NavBackStack<NavKey>,
    nowPlaying: NowPlaying,
    hazeState: HazeState,
    onPlayStation: (stations: List<RadioStation>, index: Int) -> Unit,
    onPlaylistUpdate: (List<RadioStation>) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavDisplay(
        modifier = modifier.fillMaxSize(),
        backStack = backStack,
        entryProvider = entryProvider {
            entry<Routes.Home> {
                HomeScreen(
                    nowPlaying = nowPlaying,
                    hazeState = hazeState,
                    onStationClick = onPlayStation,
                    onFavoritesClick = { backStack.navigate(Routes.Favorites) },
                )
            }
            entry<Routes.Favorites> {
                FavoritesScreen(
                    nowPlaying = nowPlaying,
                    hazeState = hazeState,
                    onStationClick = onPlayStation,
                    onPlaylistUpdate = onPlaylistUpdate,
                    onBack = backStack::popUp,
                )
            }
        },
        transitionSpec = {
            slideInHorizontally(initialOffsetX = { it }) togetherWith
                slideOutHorizontally(targetOffsetX = { -it })
        },
        popTransitionSpec = {
            slideInHorizontally(initialOffsetX = { -it }) togetherWith
                slideOutHorizontally(targetOffsetX = { it })
        },
        predictivePopTransitionSpec = {
            slideInHorizontally(initialOffsetX = { -it }) togetherWith
                slideOutHorizontally(targetOffsetX = { it })
        },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
    )
}
