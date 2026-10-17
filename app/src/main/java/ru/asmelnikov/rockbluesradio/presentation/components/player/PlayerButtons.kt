package ru.asmelnikov.rockbluesradio.presentation.components.player

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import ru.asmelnikov.rockbluesradio.R
import ru.asmelnikov.rockbluesradio.presentation.components.common.rememberLottieTint
import ru.asmelnikov.rockbluesradio.presentation.theme.RockBluesRadioTheme

private val PauseToPlayFrames = LottieClipSpec.Frame(0, 37)
private val PlayToPauseFrames = LottieClipSpec.Frame(38, 67)
private const val ANIMATION_END_PROGRESS = 0.99f

enum class SkipDirection { Previous, Next }

@Composable
fun PlayPauseButton(
    isPlaying: Boolean,
    isBuffering: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: Color = MaterialTheme.colorScheme.onSurface,
) {
    IconButton(
        modifier = modifier,
        onClick = onClick,
    ) {
        AnimatedContent(
            targetState = isBuffering,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
        ) { buffering ->
            if (buffering) {
                BufferingIndicator(iconTint)
            } else {
                PlayPauseIcon(isPlaying, iconTint)
            }
        }
    }
}

@Composable
fun SkipButton(
    direction: SkipDirection,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: Color = MaterialTheme.colorScheme.onSurface,
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.next_track))
    var isAnimating by remember { mutableStateOf(false) }
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isAnimating,
        restartOnPlay = true,
    )

    LaunchedEffect(progress) {
        if (progress >= ANIMATION_END_PROGRESS && isAnimating) {
            isAnimating = false
        }
    }

    IconButton(
        modifier = modifier,
        onClick = {
            isAnimating = true
            onClick()
        },
    ) {
        LottieAnimation(
            modifier = Modifier.graphicsLayer {
                rotationZ = if (direction == SkipDirection.Previous) 180f else 0f
            },
            composition = composition,
            progress = { progress },
            dynamicProperties = rememberLottieTint(iconTint),
        )
    }
}

@Composable
private fun BufferingIndicator(tint: Color) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading_circle))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
    )
    LottieAnimation(
        composition = composition,
        progress = { progress },
        dynamicProperties = rememberLottieTint(tint),
    )
}

@Composable
private fun PlayPauseIcon(isPlaying: Boolean, tint: Color) {
    key(isPlaying) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.pause_play))
        val progress by animateLottieCompositionAsState(
            composition = composition,
            clipSpec = if (isPlaying) PauseToPlayFrames else PlayToPauseFrames,
            restartOnPlay = true,
        )
        LottieAnimation(
            composition = composition,
            progress = { progress },
            dynamicProperties = rememberLottieTint(tint),
        )
    }
}

@Preview
@Composable
private fun PlayerButtonsPreview() {
    RockBluesRadioTheme(darkTheme = true) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background),
        ) {
            PlayPauseButton(
                modifier = Modifier.padding(24.dp),
                isPlaying = false,
                isBuffering = false,
                onClick = {},
            )
        }
    }
}

@Preview
@Composable
private fun SkipButtonPreview() {
    RockBluesRadioTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background),
        ) {
            SkipButton(direction = SkipDirection.Next, onClick = {})
        }
    }
}
