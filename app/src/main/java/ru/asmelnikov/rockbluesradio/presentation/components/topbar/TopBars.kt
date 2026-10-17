package ru.asmelnikov.rockbluesradio.presentation.components.topbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.rememberHazeState
import ru.asmelnikov.rockbluesradio.R
import ru.asmelnikov.rockbluesradio.presentation.theme.RockBluesRadioTheme
import ru.asmelnikov.rockbluesradio.presentation.theme.dimens
import ru.asmelnikov.rockbluesradio.presentation.utils.navigationBarsPaddingIfLandscape

private const val FAVORITES_ICON_SPEED = 0.5f

@Composable
fun HomeTopBar(
    showFavoritesButton: Boolean,
    hazeState: HazeState,
    onFavoritesClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BlurredTopBar(modifier = modifier, hazeState = hazeState) {
        Row(
            modifier = Modifier
                .navigationBarsPaddingIfLandscape()
                .statusBarsPadding()
                .height(dimens.medium5)
                .padding(horizontal = dimens.small3)
                .padding(bottom = dimens.small1)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TopBarTitle(text = stringResource(R.string.app_name))
            AnimatedVisibility(
                visible = showFavoritesButton,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                FavoritesButton(onClick = onFavoritesClick)
            }
        }
    }
}

@Composable
fun FavoritesTopBar(
    hazeState: HazeState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BlurredTopBar(modifier = modifier, hazeState = hazeState) {
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = dimens.small3)
                .padding(bottom = dimens.small1)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                modifier = Modifier.size(dimens.medium4),
                onClick = onBack,
            ) {
                Icon(
                    modifier = Modifier.fillMaxSize(fraction = 0.8f),
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                )
            }
            Spacer(modifier = Modifier.width(dimens.small1))
            TopBarTitle(text = stringResource(R.string.favorite_stations))
        }
    }
}

@Composable
private fun BlurredTopBar(
    hazeState: HazeState,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .hazeEffect(state = hazeState)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.2f)),
    ) {
        content()
    }
}

@Composable
private fun TopBarTitle(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.headlineLarge,
    )
}

@Composable
private fun FavoritesButton(onClick: () -> Unit) {
    val description = stringResource(R.string.favorite_stations)
    IconButton(
        modifier = Modifier
            .size(dimens.medium4)
            .semantics { contentDescription = description },
        onClick = onClick,
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.favorite_main))
        LottieAnimation(
            modifier = Modifier.fillMaxSize(fraction = 0.8f),
            composition = composition,
            iterations = LottieConstants.IterateForever,
            speed = FAVORITES_ICON_SPEED,
        )
    }
}

@Preview(locale = "ru")
@Preview(device = "id:pixel_tablet")
@Composable
private fun HomeTopBarPreview() {
    RockBluesRadioTheme(darkTheme = true) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background),
        ) {
            HomeTopBar(
                showFavoritesButton = true,
                hazeState = rememberHazeState(),
                onFavoritesClick = {},
            )
        }
    }
}

@Preview(locale = "ru")
@Composable
private fun FavoritesTopBarPreview() {
    RockBluesRadioTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background),
        ) {
            FavoritesTopBar(
                hazeState = rememberHazeState(),
                onBack = {},
            )
        }
    }
}
