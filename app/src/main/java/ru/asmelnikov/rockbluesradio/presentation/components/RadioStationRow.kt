package ru.asmelnikov.rockbluesradio.presentation.components

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import ru.asmelnikov.rockbluesradio.R
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation
import ru.asmelnikov.rockbluesradio.presentation.theme.RockBluesRadioTheme
import ru.asmelnikov.rockbluesradio.presentation.theme.dimens
import ru.asmelnikov.rockbluesradio.presentation.utils.navigationBarsAndCutoutPaddingIfLandscape
import kotlin.random.Random

@Composable
fun RadioStationRow(
    modifier: Modifier = Modifier,
    item: RadioStation,
    currentPlayingStationId: String,
    isFavorite: Boolean,
    onFavClick: (RadioStation) -> Unit
) {

    Row(
        modifier = modifier.navigationBarsAndCutoutPaddingIfLandscape(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(end = dimens.small1),
            verticalAlignment = Alignment.CenterVertically
        ) {

            val backgroundColor = remember(item.favicon) {
                val seed = item.favicon.hashCode()
                val random = Random(seed)
                Color.hsl(
                    hue = random.nextFloat() * 360f,
                    saturation = 0.5f,
                    lightness = 0.8f,
                    alpha = 1f
                )
            }

            Card(
                modifier = Modifier
                    .size(dimens.large)
                    .aspectRatio(1f)
                    .padding(dimens.small1),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = backgroundColor
                )
            ) {
                Box {
                    val placeholderPainter = painterResource(id = R.drawable.radio)

                    AsyncImage(
                        modifier = Modifier.fillMaxSize(),
                        model = item.favicon,
                        contentDescription = null,
                        placeholder = placeholderPainter,
                        error = placeholderPainter
                    )

                    this@Card.AnimatedVisibility(
                        visible = item.id == currentPlayingStationId,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        val composition by rememberLottieComposition(
                            LottieCompositionSpec.RawRes(R.raw.playing)
                        )
                        LottieAnimation(
                            modifier = Modifier.fillMaxSize(),
                            composition = composition,
                            iterations = LottieConstants.IterateForever
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .padding(start = dimens.small1)
                    .weight(1f),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = item.name,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                val genres = item.genres
                if (genres.isNotEmpty()) {
                    Text(
                        text = genres.joinToString("|"),
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        FavoriteButton(
            modifier = Modifier
                .padding(end = dimens.extraSmall1)
                .size(dimens.medium4),
            isFavorite = isFavorite,
            itemId = item.id,
            onFavClick = { onFavClick(item) }
        )
    }
}

@Preview
@Preview(device = "id:pixel_tablet")
@Composable
fun RadioStationRowPreview1(modifier: Modifier = Modifier) {
    RockBluesRadioTheme(darkTheme = true) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            RadioStationRow(
                modifier = Modifier,
                item = mockRadioStation(),
                currentPlayingStationId = "",
                isFavorite = false,
                onFavClick = {}
            )
        }
    }
}

@Preview
@Preview(device = "id:pixel_tablet")
@Composable
fun RadioStationRowPreview2(modifier: Modifier = Modifier) {
    RockBluesRadioTheme {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            RadioStationRow(
                modifier = Modifier,
                item = mockRadioStation(),
                currentPlayingStationId = "",
                isFavorite = true,
                onFavClick = {}
            )
        }
    }
}