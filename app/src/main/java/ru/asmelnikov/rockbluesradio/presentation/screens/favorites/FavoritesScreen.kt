package ru.asmelnikov.rockbluesradio.presentation.screens.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import org.koin.androidx.compose.koinViewModel
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation
import ru.asmelnikov.rockbluesradio.presentation.components.FavoriteTopAppBar
import ru.asmelnikov.rockbluesradio.presentation.components.RadioStationRow
import ru.asmelnikov.rockbluesradio.presentation.navigation.popUp

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel = koinViewModel(),
    navController: NavBackStack<NavKey>,
    onFavoriteItemClick: (List<RadioStation>, Int) -> Unit = { _, _ -> }
) {
    val favoriteStations by viewModel.favorites.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            FavoriteTopAppBar {
                navController.popUp()
            }
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            RadioStationList(
                modifier = Modifier
                    .padding(paddingValues),
                items = favoriteStations,
                onItemClick = { index ->
                    onFavoriteItemClick(favoriteStations, index)
                },
                onFavClick = {
                    viewModel.removeItem(it)
                }
            )
        }
    }
}

@Composable
fun RadioStationList(
    modifier: Modifier = Modifier,
    items: List<RadioStation>,
    onItemClick: (Int) -> Unit = {},
    onFavClick: (RadioStation) -> Unit = {}
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(items = items) { item ->
            RadioStationRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(75.dp)
                    .background(Color.White)
                    .padding(16.dp)
                    .clickable {
                        onItemClick(items.indexOf(item))
                    },
                item = item,
                isFavorite = item.isFavorite,
                onFavClick = onFavClick
            )
        }
    }
}