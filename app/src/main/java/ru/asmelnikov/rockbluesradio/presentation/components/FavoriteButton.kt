package ru.asmelnikov.rockbluesradio.presentation.components

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.toArgb
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import ru.asmelnikov.rockbluesradio.R

@Composable
fun FavoriteButton(
    modifier: Modifier = Modifier,
    isFavorite: Boolean,
    itemId: String,
    iconTint: Color = Color.Unspecified,
    onFavClick: () -> Unit
) {
    var animationKey by remember(itemId) { mutableIntStateOf(0) }
    var isAnimating by remember(itemId) { mutableStateOf(false) }
    var animateToFavorite by remember(itemId) { mutableStateOf(isFavorite) }

    LaunchedEffect(itemId, isFavorite) {
        if (animateToFavorite != isFavorite) {
            animateToFavorite = isFavorite
            animationKey++
            isAnimating = true
        }
    }

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.favorite_unfavorite)
    )

    val dynamicProperties = rememberLottieDynamicProperties(
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR_FILTER,
            value = PorterDuffColorFilter(iconTint.toArgb(), PorterDuff.Mode.SRC_ATOP),
            keyPath = arrayOf("**")
        )
    )

    IconButton(
        modifier = modifier,
        onClick = {
            animateToFavorite = !isFavorite
            animationKey++
            isAnimating = true
            onFavClick()
        }
    ) {
        key(itemId, animationKey) {
            val progress by animateLottieCompositionAsState(
                composition = composition,
                iterations = 1,
                isPlaying = isAnimating,
                speed = 1f,
                clipSpec = if (animateToFavorite) {
                    LottieClipSpec.Frame(0, 74)
                } else {
                    LottieClipSpec.Frame(74, 114)
                },
                restartOnPlay = false
            )

            LaunchedEffect(progress) {
                if (progress >= 0.99f && isAnimating) {
                    isAnimating = false
                }
            }

            LottieAnimation(
                modifier = Modifier.fillMaxSize(fraction = 0.8f),
                composition = composition,
                progress = {
                    if (isAnimating) progress
                    else if (isFavorite) 74f / 129f
                    else 0f
                },
                dynamicProperties = dynamicProperties
            )
        }
    }
}


