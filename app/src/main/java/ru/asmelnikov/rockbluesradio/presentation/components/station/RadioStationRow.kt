package ru.asmelnikov.rockbluesradio.presentation.components.station

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import ru.asmelnikov.rockbluesradio.R
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation
import ru.asmelnikov.rockbluesradio.presentation.components.common.StationArtwork
import ru.asmelnikov.rockbluesradio.presentation.preview.PreviewData
import ru.asmelnikov.rockbluesradio.presentation.theme.RockBluesRadioTheme
import ru.asmelnikov.rockbluesradio.presentation.theme.dimens
import ru.asmelnikov.rockbluesradio.presentation.utils.navigationBarsAndCutoutPaddingIfLandscape

private const val GENRES_SEPARATOR = "|"

@Composable
fun RadioStationRow(
    station: RadioStation,
    isPlaying: Boolean,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.navigationBarsAndCutoutPaddingIfLandscape(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(end = dimens.small1),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StationArtwork(
                modifier = Modifier
                    .size(dimens.large)
                    .aspectRatio(1f)
                    .padding(dimens.small1),
                artworkUrl = station.faviconUrl,
            ) {
                PlayingIndicator(visible = isPlaying)
            }
            StationInfo(
                modifier = Modifier
                    .padding(start = dimens.small1)
                    .weight(1f),
                station = station,
            )
        }
        FavoriteButton(
            modifier = Modifier
                .padding(end = dimens.extraSmall1)
                .size(dimens.medium4),
            isFavorite = station.isFavorite,
            animationKey = station.id,
            onClick = onFavoriteClick,
        )
    }
}

@Composable
private fun StationInfo(station: RadioStation, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = station.name,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (station.genres.isNotEmpty()) {
            Text(
                text = station.genres.joinToString(GENRES_SEPARATOR),
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun PlayingIndicator(visible: Boolean) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.playing))
        LottieAnimation(
            modifier = Modifier.fillMaxSize(),
            composition = composition,
            iterations = LottieConstants.IterateForever,
        )
    }
}

@Preview
@Preview(device = "id:pixel_tablet")
@Composable
private fun RadioStationRowPreview() {
    RockBluesRadioTheme(darkTheme = true) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background),
        ) {
            RadioStationRow(
                station = PreviewData.station(),
                isPlaying = false,
                onFavoriteClick = {},
            )
        }
    }
}
