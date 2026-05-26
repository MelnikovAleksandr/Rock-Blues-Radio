package ru.asmelnikov.rockbluesradio.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.asmelnikov.rockbluesradio.R
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation
import kotlin.random.Random

@Composable
fun RadioStationRow(
    modifier: Modifier = Modifier,
    item: RadioStation,
    isFavorite: Boolean,
    onFavClick: (RadioStation) -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
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
                modifier = Modifier.fillMaxWidth(fraction = 0.2f).aspectRatio(1f).padding(8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = backgroundColor
                )
            ) {
                val placeholderPainter = painterResource(id = R.drawable.radio)

                AsyncImage(
                    modifier = Modifier
                        .fillMaxSize(),
                    model = item.favicon,
                    contentDescription = null,
                    placeholder = placeholderPainter,
                    error = placeholderPainter
                )
            }

            Column(
                modifier = Modifier
                    .padding(start = 8.dp)
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
            isFavorite = isFavorite,
            itemId = item.id,
            iconTint = MaterialTheme.colorScheme.primary,
            onFavClick = { onFavClick(item) }
        )
    }
}