package ru.asmelnikov.rockbluesradio.presentation.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation
import ru.asmelnikov.rockbluesradio.presentation.screens.favorites.FavoritesScreen
import ru.asmelnikov.rockbluesradio.presentation.screens.main.HomeScreen

@Composable
fun NavGraph(
    backStack: NavBackStack<NavKey>,
    paddingValues: PaddingValues,
    onNextPage: (List<RadioStation>) -> Unit = {},
    onRadioStationClick: (Int) -> Unit = {},
    onFavoriteItemClick: (List<RadioStation>, Int) -> Unit = { _, _ -> },
    isPlayerSetUp: Boolean = false
) {
    NavDisplay(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        backStack = backStack,
        entryProvider = entryProvider {
            entry<Routes.MainScreen> {
                HomeScreen(
                    navController = backStack,
                    onRadioStationClick = onRadioStationClick,
                    onNextPage = onNextPage,
                    isPlayerSetUp = isPlayerSetUp
                )
            }

            entry<Routes.FavoritesScreen> {
                FavoritesScreen(
                    navController = backStack,
                    onFavoriteItemClick = onFavoriteItemClick
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
            rememberViewModelStoreNavEntryDecorator()
        )
    )
}