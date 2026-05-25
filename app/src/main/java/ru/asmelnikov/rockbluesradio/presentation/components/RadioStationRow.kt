package ru.asmelnikov.rockbluesradio.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.asmelnikov.rockbluesradio.R
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation


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
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth(fraction = 0.2f)
                    .aspectRatio(1f)
                    .padding(8.dp),
                model = item.favicon,
                contentDescription = null,
                placeholder = painterResource(id = R.drawable.radio),
                error = painterResource(id = R.drawable.error)
            )
            Column(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = item.name,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                val genres = item.genres
                if (genres.isNotEmpty()) {
                    Text(
                        text = genres.joinToString("|"),
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        IconButton(onClick = { onFavClick(item)}) {
            Icon(
                imageVector = if (isFavorite) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites"
            )
        }
    }
}