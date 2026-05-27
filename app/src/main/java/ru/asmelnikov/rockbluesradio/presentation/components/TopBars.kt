package ru.asmelnikov.rockbluesradio.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.rememberHazeState
import ru.asmelnikov.rockbluesradio.R
import ru.asmelnikov.rockbluesradio.presentation.theme.RockBluesRadioTheme
import ru.asmelnikov.rockbluesradio.presentation.theme.dimens
import ru.asmelnikov.rockbluesradio.presentation.utils.navigationBarsPaddingIfLandscape

@Composable
fun HomeTopAppBar(
    modifier: Modifier = Modifier,
    hazeState: HazeState,
    onFavClick: () -> Unit
) {
    Column(
        modifier = modifier
            .hazeEffect(state = hazeState)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.2f))
    ) {

        Row(
            modifier = Modifier
                .navigationBarsPaddingIfLandscape()
                .statusBarsPadding()
                .padding(horizontal = dimens.small3)
                .padding(bottom = dimens.small1)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.radio_stations),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineLarge
            )
            IconButton(
                modifier = Modifier.size(dimens.medium4),
                onClick = onFavClick
            ) {
                Icon(
                    modifier = Modifier.fillMaxSize(fraction = 0.8f),
                    imageVector = Icons.Outlined.Favorite,
                    contentDescription = "Favorites"
                )
            }
        }
    }
}

@Composable
fun FavoriteTopAppBar(
    modifier: Modifier = Modifier,
    hazeState: HazeState,
    navigateUp: () -> Unit
) {
    Column(
        modifier = modifier
            .hazeEffect(state = hazeState)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.2f))
    ) {

        Row(
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = dimens.small3)
                .padding(bottom = dimens.small1)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                modifier = Modifier.size(dimens.medium4),
                onClick = navigateUp
            ) {
                Icon(
                    modifier = Modifier.fillMaxSize(fraction = 0.8f),
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Spacer(modifier = Modifier.width(dimens.small1))
            Text(
                text = stringResource(R.string.favorite_stations),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineLarge
            )
        }
    }
}

@Preview(locale = "ru")
@Preview(device = "id:pixel_tablet")
@Composable
fun HomeTopAppBarPreview1(modifier: Modifier = Modifier) {
    RockBluesRadioTheme(darkTheme = true) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            HomeTopAppBar(
                hazeState = rememberHazeState(),
                onFavClick = {}
            )
        }
    }
}

@Preview(locale = "ru")
@Preview(device = "id:pixel_tablet")
@Composable
fun FavoriteTopAppBarPreview1(modifier: Modifier = Modifier) {
    RockBluesRadioTheme {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            FavoriteTopAppBar(
                hazeState = rememberHazeState(),
                navigateUp = {}
            )
        }
    }
}