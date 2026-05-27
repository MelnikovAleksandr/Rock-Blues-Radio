package ru.asmelnikov.rockbluesradio.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation
import ru.asmelnikov.rockbluesradio.presentation.screens.main.ScreenState
import ru.asmelnikov.rockbluesradio.presentation.theme.RockBluesRadioTheme

@Composable
fun RadioStationList(
    modifier: Modifier = Modifier,
    state: ScreenState,
    hazeState: HazeState,
    isPlayerSetUp: Boolean,
    onItemClick: (Int) -> Unit = {},
    onFavoritesClick: () -> Unit = {},
    onFavClick: (RadioStation) -> Unit = {}
) {
    Box(modifier = modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .hazeSource(state = hazeState)
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
        )

        LazyColumn(
            modifier = Modifier
        ) {

            stickyHeader {
                HomeTopAppBar(
                    hazeState = hazeState,
                ) {
                    onFavoritesClick()
                }
            }

            itemsIndexed(items = state.items, key = { _, it -> it.id }) { index, item ->
                RadioStationRow(
                    modifier = Modifier
                        .hazeSource(state = hazeState)
                        .animateItem()
                        .clickable {
                            onItemClick(index)
                        }
                        .fillMaxWidth(),
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
}

@Preview(locale = "ru")
@Preview(locale = "ru", device = "spec:parent=pixel_5,orientation=landscape")
@Composable
fun RadioStationListPreview1(modifier: Modifier = Modifier) {
    RockBluesRadioTheme(darkTheme = true) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            RadioStationList(
                state = ScreenState(
                    items = mockRadioStations(),
                ),
                hazeState = rememberHazeState(),
                isPlayerSetUp = false,
                onItemClick = {},
                onFavoritesClick = {},
                onFavClick = {}
            )
        }
    }
}

@Preview(locale = "ru")
@Preview(locale = "ru", device = "spec:parent=pixel_5,orientation=landscape")
@Composable
fun RadioStationListPreview2(modifier: Modifier = Modifier) {
    RockBluesRadioTheme {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            RadioStationList(
                state = ScreenState(
                    items = mockRadioStations(),
                ),
                hazeState = rememberHazeState(),
                isPlayerSetUp = false,
                onItemClick = {},
                onFavoritesClick = {},
                onFavClick = {}
            )
        }
    }
}