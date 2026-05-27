package ru.asmelnikov.rockbluesradio.presentation.screens.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import dev.chrisbanes.haze.HazeState
import org.koin.androidx.compose.koinViewModel
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation
import ru.asmelnikov.rockbluesradio.presentation.components.RadioStationList
import ru.asmelnikov.rockbluesradio.presentation.navigation.Routes
import ru.asmelnikov.rockbluesradio.presentation.navigation.navigate

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    navController: NavBackStack<NavKey>,
    onRadioStationClick: (Int) -> Unit = {},
    onItemsUpdate: (List<RadioStation>) -> Unit = {},
    isPlayerSetUp: Boolean = false,
    hazeState: HazeState
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    RadioStationList(
        state = state,
        hazeState = hazeState,
        isPlayerSetUp = isPlayerSetUp,
        onFavoritesClick = {
            navController.navigate(Routes.FavoritesScreen)
        },
        onItemClick = { index ->
            onItemsUpdate(state.items)
            onRadioStationClick(index)
        },
        onFavClick = {
            viewModel.addOrRemoteFavorites(it)
        }
    )
}