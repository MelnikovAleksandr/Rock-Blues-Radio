package ru.asmelnikov.rockbluesradio.presentation.components.topbar

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import kotlin.math.abs
import ru.asmelnikov.rockbluesradio.R
import ru.asmelnikov.rockbluesradio.domain.model.Genre
import ru.asmelnikov.rockbluesradio.presentation.theme.dimens

private val PillCornerRadius = 999.dp
private const val SELECTED_FONT_THRESHOLD = 0.5f

@Composable
fun GenreSwitcher(
    genres: List<Genre>,
    pagerProgress: Float,
    hazeState: HazeState,
    onGenreClick: (Genre) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(percent = 50)
    Row(
        modifier = modifier
            .padding(dimens.small2)
            .background(Color.Transparent, shape)
            .border(dimens.borderSize, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), shape),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        genres.forEachIndexed { index, genre ->
            GenreSegment(
                modifier = Modifier.weight(1f),
                hazeState = hazeState,
                text = stringResource(genre.titleRes),
                selectionProgress = (1f - abs(pagerProgress - index)).coerceIn(0f, 1f),
                shape = segmentShape(isFirst = index == 0, isLast = index == genres.lastIndex),
                onClick = { onGenreClick(genre) },
            )
        }
    }
}

@Composable
private fun GenreSegment(
    hazeState: HazeState,
    text: String,
    selectionProgress: Float,
    shape: RoundedCornerShape,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
    val unselectedColor = MaterialTheme.colorScheme.background.copy(alpha = 0.1f)
    Box(
        modifier = modifier
            .clip(shape)
            .hazeEffect(state = hazeState)
            .background(color = lerp(unselectedColor, selectedColor, selectionProgress), shape = shape)
            .clickable(onClick = onClick)
            .padding(vertical = dimens.small1),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = if (selectionProgress >= SELECTED_FONT_THRESHOLD) FontWeight.SemiBold else FontWeight.Normal,
        )
    }
}

private fun segmentShape(isFirst: Boolean, isLast: Boolean) = RoundedCornerShape(
    topStart = if (isFirst) PillCornerRadius else 0.dp,
    bottomStart = if (isFirst) PillCornerRadius else 0.dp,
    topEnd = if (isLast) PillCornerRadius else 0.dp,
    bottomEnd = if (isLast) PillCornerRadius else 0.dp,
)

@get:StringRes
private val Genre.titleRes: Int
    get() = when (this) {
        Genre.Rock -> R.string.rock
        Genre.Blues -> R.string.blues
    }
