package ru.asmelnikov.rockbluesradio.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation
import ru.asmelnikov.rockbluesradio.presentation.screens.main.ScreenState

@Composable
fun RadioStationList(
    modifier: Modifier = Modifier,
    state: ScreenState,
    isPlayerSetUp: Boolean,
    onItemClick: (Int) -> Unit = {},
    onFavoritesClick: () -> Unit = {},
    onFavClick: (RadioStation) -> Unit = {}
) {
    val scrollState = rememberLazyListState()
    LazyColumn(
        modifier = modifier,
        state = scrollState
    ) {

        stickyHeader {
            HomeTopAppBar {
                onFavoritesClick()
            }
        }

        itemsIndexed(items = state.items, key = { _, it -> it.id }) { index, item ->
            RadioStationRow(
                modifier = Modifier
                    .animateItem()
                    .clickable {
                        onItemClick(index)
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