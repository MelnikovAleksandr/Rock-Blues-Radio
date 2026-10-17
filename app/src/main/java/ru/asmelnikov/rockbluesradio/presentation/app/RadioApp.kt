package ru.asmelnikov.rockbluesradio.presentation.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.rememberNavBackStack
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.rememberHazeState
import ru.asmelnikov.rockbluesradio.R
import ru.asmelnikov.rockbluesradio.presentation.components.dialog.NoInternetDialog
import ru.asmelnikov.rockbluesradio.presentation.components.player.MiniPlayer
import ru.asmelnikov.rockbluesradio.presentation.components.player.expanded.ExpandedPlayerSheet
import ru.asmelnikov.rockbluesradio.presentation.navigation.NavGraph
import ru.asmelnikov.rockbluesradio.presentation.navigation.Routes
import ru.asmelnikov.rockbluesradio.presentation.player.NowPlaying
import ru.asmelnikov.rockbluesradio.presentation.player.PlayerState
import ru.asmelnikov.rockbluesradio.presentation.player.rememberMediaController
import ru.asmelnikov.rockbluesradio.presentation.player.rememberPlayerState
import ru.asmelnikov.rockbluesradio.presentation.theme.dimens

private const val MINI_PLAYER_ANIMATION_MILLIS = 300

@Composable
fun RadioApp(viewModel: MainViewModel) {
    val isConnected by viewModel.isConnected.collectAsStateWithLifecycle()
    val playerState = rememberPlayerState(rememberMediaController())

    val backStack = rememberNavBackStack(Routes.Home)
    val snackbarHostState = remember { SnackbarHostState() }
    val hazeState = rememberHazeState()
    var isExpandedPlayerOpen by rememberSaveable { mutableStateOf(false) }
    val hasMediaItem = playerState?.currentMediaItem != null

    PlayerErrorEffect(playerState = playerState, snackbarHostState = snackbarHostState)

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentWindowInsets = WindowInsets(0),
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.navigationBarsPadding(),
            )
        },
    ) { innerPadding ->
        if (!isConnected) {
            NoInternetDialog()
        }

        if (isExpandedPlayerOpen && playerState != null) {
            ExpandedPlayerSheet(
                playerState = playerState,
                onDismiss = { isExpandedPlayerOpen = false },
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            NavGraph(
                backStack = backStack,
                nowPlaying = NowPlaying(
                    stationId = playerState?.playingStationId,
                    isMiniPlayerVisible = hasMediaItem,
                ),
                hazeState = hazeState,
                onPlayStation = { folder, stations, index ->
                    playerState?.playStation(folder, stations, index)
                },
            )
            MiniPlayerBar(
                modifier = Modifier.align(Alignment.BottomCenter),
                visible = hasMediaItem && !isExpandedPlayerOpen,
                playerState = playerState,
                hazeState = hazeState,
                onClick = { isExpandedPlayerOpen = true },
            )
        }
    }
}

@Composable
private fun PlayerErrorEffect(playerState: PlayerState?, snackbarHostState: SnackbarHostState) {
    val errorMessage = stringResource(R.string.error)
    val retryLabel = stringResource(R.string.error_retry)
    val playerError = playerState?.playerError

    LaunchedEffect(playerError) {
        if (playerError == null) return@LaunchedEffect
        val result = snackbarHostState.showSnackbar(
            message = "$errorMessage ${playerError.message}, Code: ${playerError.errorCode}",
            actionLabel = retryLabel,
            withDismissAction = true,
        )
        if (result == SnackbarResult.ActionPerformed) {
            playerState?.retry()
        }
    }
}

@Composable
private fun MiniPlayerBar(
    visible: Boolean,
    playerState: PlayerState?,
    hazeState: HazeState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        modifier = modifier
            .padding(dimens.extraSmall2)
            .navigationBarsPadding(),
        visible = visible && playerState != null,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(MINI_PLAYER_ANIMATION_MILLIS),
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(MINI_PLAYER_ANIMATION_MILLIS),
        ) + fadeOut(),
    ) {
        playerState?.let { state ->
            MiniPlayer(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onClick),
                mediaItem = state.currentMediaItem,
                isPlaying = state.isPlaying,
                isBuffering = state.isBuffering,
                hazeState = hazeState,
                onPlayPauseClick = state::togglePlayPause,
            )
        }
    }
}
