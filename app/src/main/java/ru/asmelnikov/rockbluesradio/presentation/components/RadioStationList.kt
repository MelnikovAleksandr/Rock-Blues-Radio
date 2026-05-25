package ru.asmelnikov.rockbluesradio.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation
import ru.asmelnikov.rockbluesradio.presentation.screens.main.ScreenState

@Composable
fun RadioStationList(
    modifier: Modifier = Modifier,
    state: ScreenState,
    onItemClick: (Int) -> Unit = {},
    onFavClick: (RadioStation) -> Unit = {}
) {
    val scrollState = rememberLazyListState()
    LazyColumn(
        modifier = modifier,
        state = scrollState
    ) {
        items(
            count = state.items.size
        ) { index ->

            state.items[index].let { item ->
                RadioStationRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(75.dp)
                        .background(Color.White)
                        .padding(16.dp)
                        .clickable {
                            onItemClick(index)
                        },
                    item = item,
                    isFavorite = item.isFavorite,
                    onFavClick = onFavClick
                )
            }
        }
    }
}