package ru.asmelnikov.rockbluesradio.presentation.components

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import ru.asmelnikov.rockbluesradio.R
import ru.asmelnikov.rockbluesradio.presentation.theme.RockBluesRadioTheme
import kotlin.random.Random

@Composable
fun PlayPauseButton(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    isBuffering: Boolean,
    iconTint: Color = MaterialTheme.colorScheme.onSurface,
    onTogglePlayPause: () -> Unit
) {
    IconButton(
        modifier = modifier,
        onClick = onTogglePlayPause
    ) {
        AnimatedContent(
            targetState = isBuffering,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            }
        ) { isBufferingState ->
            if (isBufferingState) {
                val composition by rememberLottieComposition(
                    LottieCompositionSpec.RawRes(R.raw.loading_circle)
                )

                val dynamicProperties = rememberLottieDynamicProperties(
                    rememberLottieDynamicProperty(
                        property = LottieProperty.COLOR_FILTER,
                        value = PorterDuffColorFilter(iconTint.toArgb(), PorterDuff.Mode.SRC_ATOP),
                        keyPath = arrayOf("**")
                    )
                )

                val progress by animateLottieCompositionAsState(
                    composition = composition,
                    iterations = LottieConstants.IterateForever,
                    isPlaying = true,
                    speed = 1f
                )

                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    dynamicProperties = dynamicProperties
                )
            } else {
                key(isPlaying) {
                    val composition by rememberLottieComposition(
                        LottieCompositionSpec.RawRes(R.raw.pause_play)
                    )

                    val dynamicProperties = rememberLottieDynamicProperties(
                        rememberLottieDynamicProperty(
                            property = LottieProperty.COLOR_FILTER,
                            value = PorterDuffColorFilter(
                                iconTint.toArgb(),
                                PorterDuff.Mode.SRC_ATOP
                            ),
                            keyPath = arrayOf("**")
                        )
                    )

                    val clipSpec = if (isPlaying) {
                        LottieClipSpec.Frame(0, 37)
                    } else {
                        LottieClipSpec.Frame(38, 67)
                    }

                    val progress by animateLottieCompositionAsState(
                        composition = composition,
                        iterations = 1,
                        isPlaying = true,
                        speed = 1f,
                        clipSpec = clipSpec,
                        restartOnPlay = true
                    )

                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        dynamicProperties = dynamicProperties
                    )
                }
            }
        }
    }
}

@Composable
fun MiniPlayerArtworkView(
    modifier: Modifier = Modifier,
    artworkUri: Uri?
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium
    ) {
        val placeholderPainter = painterResource(id = R.drawable.radio)
        val backgroundColor = remember(artworkUri) {
            val seed = artworkUri.hashCode()
            val random = Random(seed)
            Color.hsl(
                hue = random.nextFloat() * 360f,
                saturation = 0.5f,
                lightness = 0.8f,
                alpha = 1f
            )
        }
        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = backgroundColor
                ),
            model = artworkUri,
            contentDescription = null,
            placeholder = placeholderPainter,
            error = placeholderPainter
        )
    }
}

@Composable
fun PreviousButton(
    modifier: Modifier = Modifier,
    iconTint: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.next_track)
    )

    val dynamicProperties = rememberLottieDynamicProperties(
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR_FILTER,
            value = PorterDuffColorFilter(iconTint.toArgb(), PorterDuff.Mode.SRC_ATOP),
            keyPath = arrayOf("**")
        )
    )

    var shouldAnimate by remember { mutableStateOf(false) }

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1,
        isPlaying = shouldAnimate,
        speed = 1f,
        restartOnPlay = true
    )

    LaunchedEffect(progress) {
        if (progress >= 0.99f && shouldAnimate) {
            shouldAnimate = false
        }
    }

    IconButton(
        modifier = modifier,
        onClick = {
            shouldAnimate = true
            onClick()
        }
    ) {
        LottieAnimation(
            modifier = Modifier
                .graphicsLayer {
                    rotationZ = 180f
                },
            composition = composition,
            progress = { progress },
            dynamicProperties = dynamicProperties
        )
    }
}

@Composable
fun NextButton(
    modifier: Modifier = Modifier,
    iconTint: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.next_track)
    )

    val dynamicProperties = rememberLottieDynamicProperties(
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR_FILTER,
            value = PorterDuffColorFilter(iconTint.toArgb(), PorterDuff.Mode.SRC_ATOP),
            keyPath = arrayOf("**")
        )
    )

    var shouldAnimate by remember { mutableStateOf(false) }

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1,
        isPlaying = shouldAnimate,
        speed = 1f,
        restartOnPlay = true
    )

    LaunchedEffect(progress) {
        if (progress >= 0.99f && shouldAnimate) {
            shouldAnimate = false
        }
    }

    IconButton(
        modifier = modifier,
        onClick = {
            shouldAnimate = true
            onClick()
        }
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            dynamicProperties = dynamicProperties
        )
    }
}

@Preview
@Composable
fun PlayPauseButtonPreview1(modifier: Modifier = Modifier) {
    RockBluesRadioTheme(darkTheme = true) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            PlayPauseButton(
                modifier = Modifier.padding(24.dp),
                isPlaying = false,
                isBuffering = false,
                onTogglePlayPause = {}
            )
        }
    }
}

@Preview
@Composable
fun PlayPauseButtonPreview2(modifier: Modifier = Modifier) {
    RockBluesRadioTheme {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            PlayPauseButton(
                modifier = Modifier.padding(24.dp),
                isPlaying = false,
                isBuffering = false,
                onTogglePlayPause = {}
            )
        }
    }
}

@Preview
@Composable
fun MiniPlayerArtworkViewPreview1(modifier: Modifier = Modifier) {
    RockBluesRadioTheme(darkTheme = true) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            MiniPlayerArtworkView(
                modifier = Modifier
                    .padding(24.dp)
                    .aspectRatio(1f),
                artworkUri = Uri.EMPTY
            )
        }
    }
}

@Preview
@Composable
fun PreviousButtonPreview1(modifier: Modifier = Modifier) {
    RockBluesRadioTheme(darkTheme = true) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            PreviousButton(
                onClick = {}
            )
        }
    }
}

@Preview
@Composable
fun NextButtonPreview1(modifier: Modifier = Modifier) {
    RockBluesRadioTheme {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            NextButton(
                onClick = {}
            )
        }
    }
}