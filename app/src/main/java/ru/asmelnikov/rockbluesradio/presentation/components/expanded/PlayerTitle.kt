package ru.asmelnikov.rockbluesradio.presentation.components.expanded

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import ru.asmelnikov.rockbluesradio.presentation.theme.dimens

@Composable
fun PlayerTitle(title: String) {
    Text(
        modifier = Modifier
            .padding(horizontal = dimens.medium1)
            .basicMarquee(Int.MAX_VALUE),
        text = title,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.displaySmall
    )
}