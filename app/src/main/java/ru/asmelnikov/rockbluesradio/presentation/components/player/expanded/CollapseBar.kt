package ru.asmelnikov.rockbluesradio.presentation.components.player.expanded

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import ru.asmelnikov.rockbluesradio.R
import ru.asmelnikov.rockbluesradio.presentation.theme.dimens

@Composable
fun CollapseBar(onCollapse: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(dimens.small1),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(
            modifier = Modifier.size(dimens.medium4),
            onClick = onCollapse,
        ) {
            Icon(
                modifier = Modifier.fillMaxSize(0.8f),
                painter = painterResource(R.drawable.keyboard_arrow_down),
                contentDescription = "Collapse",
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
