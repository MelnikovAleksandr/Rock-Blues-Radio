package ru.asmelnikov.rockbluesradio.presentation.components.station

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import ru.asmelnikov.rockbluesradio.R
import ru.asmelnikov.rockbluesradio.presentation.components.common.rememberLottieTint
import ru.asmelnikov.rockbluesradio.presentation.theme.RockBluesRadioTheme

private const val FAVORITE_FRAME = 74
private const val LAST_FRAME = 114
private const val TOTAL_FRAMES = 129f
private const val FAVORITE_PROGRESS = FAVORITE_FRAME / TOTAL_FRAMES
private const val ANIMATION_END_PROGRESS = 0.99f
private val ToFavoriteFrames = LottieClipSpec.Frame(0, FAVORITE_FRAME)
private val ToNotFavoriteFrames = LottieClipSpec.Frame(FAVORITE_FRAME, LAST_FRAME)

@Composable
fun FavoriteButton(
    isFavorite: Boolean,
    animationKey: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: Color = MaterialTheme.colorScheme.primary,
) {
    var animationRun by remember(animationKey) { mutableIntStateOf(0) }
    var isAnimating by remember(animationKey) { mutableStateOf(false) }
    var animateToFavorite by remember(animationKey) { mutableStateOf(isFavorite) }

    LaunchedEffect(animationKey, isFavorite) {
        if (animateToFavorite != isFavorite) {
            animateToFavorite = isFavorite
            animationRun++
            isAnimating = true
        }
    }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.favorite_unfavorite))
    val tint = rememberLottieTint(iconTint)

    IconButton(
        modifier = modifier,
        onClick = {
            animateToFavorite = !isFavorite
            animationRun++
            isAnimating = true
            onClick()
        },
    ) {
        key(animationKey, animationRun) {
            val progress by animateLottieCompositionAsState(
                composition = composition,
                isPlaying = isAnimating,
                clipSpec = if (animateToFavorite) ToFavoriteFrames else ToNotFavoriteFrames,
                restartOnPlay = false,
            )

            LaunchedEffect(progress) {
                if (progress >= ANIMATION_END_PROGRESS && isAnimating) {
                    isAnimating = false
                }
            }

            LottieAnimation(
                modifier = Modifier.fillMaxSize(fraction = 0.8f),
                composition = composition,
                progress = {
                    when {
                        isAnimating -> progress
                        isFavorite -> FAVORITE_PROGRESS
                        else -> 0f
                    }
                },
                dynamicProperties = tint,
            )
        }
    }
}

@Preview
@Composable
private fun FavoriteButtonPreview() {
    RockBluesRadioTheme(darkTheme = true) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background),
        ) {
            FavoriteButton(isFavorite = true, animationKey = "preview", onClick = {})
        }
    }
}
