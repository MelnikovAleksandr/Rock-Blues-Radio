package ru.asmelnikov.rockbluesradio.presentation.screens.favorites

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
    isPlayerSetUp: Boolean,
    viewModel: FavoritesViewModel = koinViewModel(),
    navController: NavBackStack<NavKey>,
    onItemsUpdate: (List<RadioStation>) -> Unit = {},
    onFavoriteItemClick: (Int) -> Unit = {}
) {
    val favoriteStations by viewModel.favorites.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is FavoritesEvent.OnItemsUpdate -> {
                    onItemsUpdate(favoriteStations)
                }
            }
        }
    }

    RadioStationList(
        modifier = Modifier.fillMaxSize(),
        items = favoriteStations,
        isPlayerSetUp = isPlayerSetUp,
        onBackClick = {
            navController.popUp()
        },
        onItemClick = { index ->
            onItemsUpdate(favoriteStations)
            onFavoriteItemClick(index)
        },
        onFavClick = {
            viewModel.removeItem(it)
        }
    )
}

@Composable
fun RadioStationList(
    modifier: Modifier = Modifier,
    items: List<RadioStation>,
    isPlayerSetUp: Boolean,
    onItemClick: (Int) -> Unit = {},
    onBackClick: () -> Unit = {},
    onFavClick: (RadioStation) -> Unit = {}
) {
    LazyColumn(
        modifier = modifier
    ) {

        stickyHeader {
            FavoriteTopAppBar {
                onBackClick()
            }
        }

        items(items = items, key = { it.id }) { item ->
            RadioStationRow(
                modifier = Modifier
                    .animateItem()
                    .clickable {
                        onItemClick(items.indexOf(item))
                    }
                    .fillMaxWidth()
                    .height(75.dp)
                    .padding(16.dp),
                item = item,
                isFavorite = item.isFavorite,
                onFavClick = onFavClick
            )
        }
        item {
            Spacer(modifier = Modifier.navigationBarsPadding())
        }
        if (isPlayerSetUp) {
            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}