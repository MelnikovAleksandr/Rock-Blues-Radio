package ru.asmelnikov.rockbluesradio.presentation.components.player.expanded

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.launch
import ru.asmelnikov.rockbluesradio.presentation.player.PlayerState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandedPlayerSheet(playerState: PlayerState, onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val hideAndDismiss: () -> Unit = {
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) onDismiss()
        }
    }

    ModalBottomSheet(
        onDismissRequest = hideAndDismiss,
        sheetState = sheetState,
        shape = RectangleShape,
        contentWindowInsets = { WindowInsets(0) },
        dragHandle = null,
        sheetMaxWidth = Dp.Unspecified,
    ) {
        ExpandedPlayer(playerState = playerState, onCollapse = hideAndDismiss)
    }
}
