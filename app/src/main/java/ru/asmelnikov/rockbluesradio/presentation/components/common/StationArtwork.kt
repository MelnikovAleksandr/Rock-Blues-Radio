package ru.asmelnikov.rockbluesradio.presentation.components.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import kotlin.random.Random
import ru.asmelnikov.rockbluesradio.R

@Composable
fun StationArtwork(
    artworkUrl: String?,
    modifier: Modifier = Modifier,
    overlay: @Composable BoxScope.() -> Unit = {},
) {
    val placeholder = painterResource(R.drawable.radio)
    val backgroundColor = remember(artworkUrl) { placeholderColorFor(artworkUrl) }

    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
    ) {
        Box {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = artworkUrl,
                contentDescription = null,
                placeholder = placeholder,
                error = placeholder,
            )
            overlay()
        }
    }
}

private fun placeholderColorFor(artworkUrl: String?): Color {
    val random = Random(artworkUrl.hashCode())
    return Color.hsl(
        hue = random.nextFloat() * 360f,
        saturation = 0.5f,
        lightness = 0.8f,
    )
}
