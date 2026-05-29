package ru.asmelnikov.rockbluesradio.presentation.components.expanded

import android.net.Uri
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
import ru.asmelnikov.rockbluesradio.R
import kotlin.random.Random

@Composable
fun PlayerArtwork(
    modifier: Modifier = Modifier,
    artworkUri: Uri
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

    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = artworkUri,
            contentDescription = null,
            placeholder = placeholderPainter,
            error = placeholderPainter
        )
    }
}