package ru.asmelnikov.rockbluesradio.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.rememberHazeState
import ru.asmelnikov.rockbluesradio.R
import ru.asmelnikov.rockbluesradio.domain.model.Genre
import ru.asmelnikov.rockbluesradio.presentation.theme.RockBluesRadioTheme
import ru.asmelnikov.rockbluesradio.presentation.theme.dimens
import ru.asmelnikov.rockbluesradio.presentation.utils.navigationBarsPaddingIfLandscape
import kotlin.math.abs

@Composable
fun HomeTopAppBar(
    modifier: Modifier = Modifier,
    favoritesEnabled: Boolean,
    hazeState: HazeState,
    genre: Genre,
    onGenreClick: () -> Unit,
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
                .height(dimens.medium5)
                .padding(horizontal = dimens.small3)
                .padding(bottom = dimens.small1)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = onGenreClick
            ) {
                AnimatedContent(
                    targetState = genre
                ) { targetGenre ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(targetGenre.strRes),
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.headlineLarge
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            modifier = Modifier.fillMaxHeight(0.8f),
                            painter = painterResource(targetGenre.drawableRes),
                            tint = Color.Unspecified,
                            contentDescription = null
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = favoritesEnabled,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                IconButton(
                    modifier = Modifier.size(dimens.medium4),
                    onClick = onFavClick
                ) {
                    val composition by rememberLottieComposition(
                        LottieCompositionSpec.RawRes(R.raw.favorite_main)
                    )
                    LottieAnimation(
                        modifier = Modifier.fillMaxSize(fraction = 0.8f),
                        composition = composition,
                        iterations = LottieConstants.IterateForever,
                        speed = 0.5f
                    )
                }
            }
        }
    }
}

@Composable
fun HomeTitleBar(
    modifier: Modifier = Modifier,
    favoritesEnabled: Boolean,
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
                .height(dimens.medium5)
                .padding(horizontal = dimens.small3)
                .padding(bottom = dimens.small1)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            AnimatedVisibility(
                visible = favoritesEnabled,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                IconButton(
                    modifier = Modifier.size(dimens.medium4),
                    onClick = onFavClick
                ) {
                    val composition by rememberLottieComposition(
                        LottieCompositionSpec.RawRes(R.raw.favorite_main)
                    )
                    LottieAnimation(
                        modifier = Modifier.fillMaxSize(fraction = 0.8f),
                        composition = composition,
                        iterations = LottieConstants.IterateForever,
                        speed = 0.5f
                    )
                }
            }
        }
    }
}

@Composable
fun GenrePagerSwitcher(
    modifier: Modifier = Modifier,
    hazeState: HazeState,
    pagerProgress: Float,
    onRockClick: () -> Unit,
    onBluesClick: () -> Unit
) {
    val shape = RoundedCornerShape(percent = 50)
    val rockSelectionProgress = (1f - abs(pagerProgress - 0f)).coerceIn(0f, 1f)
    val bluesSelectionProgress = (1f - abs(pagerProgress - 1f)).coerceIn(0f, 1f)
    Row(
        modifier = modifier
            .padding(dimens.small2)
            .background(Color.Transparent, shape)
            .border(dimens.borderSize, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), shape),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GenreSegmentButton(
            modifier = Modifier.weight(1f),
            hazeState = hazeState,
            text = stringResource(R.string.rock),
            selectionProgress = rockSelectionProgress,
            shape = RoundedCornerShape(
                topStart = 999.dp,
                bottomStart = 999.dp,
                topEnd = 0.dp,
                bottomEnd = 0.dp
            ),
            onClick = onRockClick
        )
        GenreSegmentButton(
            modifier = Modifier.weight(1f),
            hazeState = hazeState,
            text = stringResource(R.string.blues),
            selectionProgress = bluesSelectionProgress,
            shape = RoundedCornerShape(
                topStart = 0.dp,
                bottomStart = 0.dp,
                topEnd = 999.dp,
                bottomEnd = 999.dp
            ),
            onClick = onBluesClick
        )
    }
}

@Composable
private fun GenreSegmentButton(
    modifier: Modifier = Modifier,
    hazeState: HazeState,
    text: String,
    selectionProgress: Float,
    shape: RoundedCornerShape,
    onClick: () -> Unit
) {
    val selectedColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
    val unselectedColor = MaterialTheme.colorScheme.background.copy(alpha = 0.1f)
    Box(
        modifier = modifier
            .clip(shape)
            .hazeEffect(state = hazeState)
            .background(
                color = lerp(unselectedColor, selectedColor, selectionProgress),
                shape = shape
            )
            .clickable(onClick = onClick)
            .padding(vertical = dimens.small1),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = if (selectionProgress >= 0.5f) FontWeight.SemiBold else FontWeight.Normal
        )
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
@Preview(device = "id:automotive_1408p_landscape_with_play")
@Composable
fun HomeTopAppBarPreview1(modifier: Modifier = Modifier) {
    RockBluesRadioTheme(darkTheme = true) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            HomeTopAppBar(
                genre = Genre.Rock,
                favoritesEnabled = true,
                hazeState = rememberHazeState(),
                onGenreClick = {},
                onFavClick = {}
            )
        }
    }
}

@Preview(locale = "ru")
@Composable
fun HomeTopAppBarPreview2(modifier: Modifier = Modifier) {
    RockBluesRadioTheme(darkTheme = true) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            HomeTopAppBar(
                genre = Genre.Blues,
                favoritesEnabled = false,
                hazeState = rememberHazeState(),
                onGenreClick = {},
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