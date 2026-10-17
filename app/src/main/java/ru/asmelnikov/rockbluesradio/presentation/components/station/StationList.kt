package ru.asmelnikov.rockbluesradio.presentation.components.station

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
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
import ru.asmelnikov.rockbluesradio.presentation.player.NowPlaying
import ru.asmelnikov.rockbluesradio.presentation.preview.PreviewData
import ru.asmelnikov.rockbluesradio.presentation.theme.RockBluesRadioTheme

private val MiniPlayerSpace = 60.dp

@Composable
fun StationList(
    stations: List<RadioStation>,
    nowPlaying: NowPlaying,
    hazeState: HazeState,
    onStationClick: (index: Int) -> Unit,
    onFavoriteClick: (RadioStation) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    stickyHeader: (@Composable () -> Unit)? = null,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
    ) {
        if (stickyHeader != null) {
            stickyHeader { stickyHeader() }
        }

        itemsIndexed(items = stations, key = { _, station -> station.id }) { index, station ->
            RadioStationRow(
                modifier = Modifier
                    .hazeSource(state = hazeState)
                    .animateItem()
                    .clickable { onStationClick(index) }
                    .fillMaxWidth(),
                station = station,
                isPlaying = station.id == nowPlaying.stationId,
                onFavoriteClick = { onFavoriteClick(station) },
            )
        }

        item { Spacer(modifier = Modifier.navigationBarsPadding()) }
        if (nowPlaying.isPlayerSetUp) {
            item { Spacer(modifier = Modifier.height(MiniPlayerSpace)) }
        }
    }
}

@Preview(locale = "ru")
@Composable
private fun StationListPreview() {
    RockBluesRadioTheme(darkTheme = true) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background),
        ) {
            StationList(
                stations = PreviewData.stations(),
                nowPlaying = NowPlaying(),
                hazeState = rememberHazeState(),
                onStationClick = {},
                onFavoriteClick = {},
            )
        }
    }
}
