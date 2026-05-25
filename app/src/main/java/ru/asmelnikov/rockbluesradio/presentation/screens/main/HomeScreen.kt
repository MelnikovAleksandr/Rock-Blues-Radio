package ru.asmelnikov.rockbluesradio.presentation.screens.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import org.koin.androidx.compose.koinViewModel
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation
import ru.asmelnikov.rockbluesradio.presentation.components.HomeTopAppBar
import ru.asmelnikov.rockbluesradio.presentation.components.RadioStationList
import ru.asmelnikov.rockbluesradio.presentation.navigation.Routes
import ru.asmelnikov.rockbluesradio.presentation.navigation.navigate

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    navController: NavBackStack<NavKey>,
    onRadioStationClick: (Int) -> Unit = {},
    onItemsUpdate: (List<RadioStation>) -> Unit = {},
    isPlayerSetUp: Boolean = false
) {

    Scaffold(
        topBar = {
            HomeTopAppBar {
                navController.navigate(Routes.FavoritesScreen)
            }
        }
    ) { paddingValues ->

        val state by viewModel.state.collectAsStateWithLifecycle()

        RadioStationList(
            modifier = Modifier
                .padding(paddingValues)
                .padding(bottom = if (isPlayerSetUp) 60.dp else 0.dp),
            state = state,
            onItemClick = { index ->
                onItemsUpdate(state.items)
                onRadioStationClick(index)
            },
            onFavClick = {
                viewModel.addOrRemoteFavorites(it)
            }
        )
    }
}