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
import androidx.media3.common.Player
import androidx.navigation3.runtime.rememberNavBackStack
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.rememberHazeState
import ru.asmelnikov.rockbluesradio.R
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation
import ru.asmelnikov.rockbluesradio.playback.toMediaItem
import ru.asmelnikov.rockbluesradio.presentation.components.dialog.NoInternetDialog
import ru.asmelnikov.rockbluesradio.presentation.components.player.MiniPlayer
import ru.asmelnikov.rockbluesradio.presentation.components.player.expanded.ExpandedPlayerSheet
import ru.asmelnikov.rockbluesradio.presentation.navigation.NavGraph
import ru.asmelnikov.rockbluesradio.presentation.navigation.Routes
import ru.asmelnikov.rockbluesradio.presentation.player.NowPlaying
import ru.asmelnikov.rockbluesradio.presentation.player.PlayerState
import ru.asmelnikov.rockbluesradio.presentation.player.playMediaAt
import ru.asmelnikov.rockbluesradio.presentation.player.rememberManagedMediaController
import ru.asmelnikov.rockbluesradio.presentation.player.rememberPlayerState
import ru.asmelnikov.rockbluesradio.presentation.player.togglePlayPause
import ru.asmelnikov.rockbluesradio.presentation.player.updatePlaylist
import ru.asmelnikov.rockbluesradio.presentation.theme.dimens

private const val MINI_PLAYER_ANIMATION_MILLIS = 300

@Composable
fun RadioApp(viewModel: MainViewModel) {
    val isPlayerSetUp by viewModel.isPlayerSetUp.collectAsStateWithLifecycle()
    val isConnected by viewModel.isConnected.collectAsStateWithLifecycle()
    val mediaController by rememberManagedMediaController()
    val playerState = rememberPlayerState(mediaController)

    val backStack = rememberNavBackStack(Routes.Home)
    val snackbarHostState = remember { SnackbarHostState() }
    val hazeState = rememberHazeState()
    var isExpandedPlayerOpen by rememberSaveable { mutableStateOf(false) }

    PlaybackRestoreEffect(
        player = mediaController,
        isPlayerSetUp = isPlayerSetUp,
        onActivePlaybackFound = viewModel::setUpPlayer,
    )
    PlayerErrorEffect(
        playerState = playerState,
        snackbarHostState = snackbarHostState,
        onRetry = { mediaController?.prepare() },
    )

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
                onPrevious = { mediaController?.seekToPreviousMediaItem() },
                onNext = { mediaController?.seekToNextMediaItem() },
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
                    isPlayerSetUp = isPlayerSetUp,
                ),
                hazeState = hazeState,
                onPlayStation = { stations, index ->
                    mediaController?.updatePlaylist(stations.toMediaItems())
                    if (!isPlayerSetUp) {
                        viewModel.setUpPlayer()
                    }
                    mediaController?.playMediaAt(index)
                },
                onPlaylistUpdate = { stations ->
                    mediaController?.updatePlaylist(stations.toMediaItems())
                },
            )
            MiniPlayerBar(
                modifier = Modifier.align(Alignment.BottomCenter),
                visible = isPlayerSetUp && !isExpandedPlayerOpen && playerState?.currentMediaItem != null,
                playerState = playerState,
                hazeState = hazeState,
                onClick = { isExpandedPlayerOpen = true },
            )
        }
    }
}

@Composable
private fun PlaybackRestoreEffect(
    player: Player?,
    isPlayerSetUp: Boolean,
    onActivePlaybackFound: () -> Unit,
) {
    LaunchedEffect(player) {
        if (player != null && (player.playWhenReady || player.playbackState == Player.STATE_READY)) {
            onActivePlaybackFound()
        }
    }
    LaunchedEffect(isPlayerSetUp) {
        if (isPlayerSetUp && player != null && player.mediaItemCount > 0) {
            player.prepare()
            player.play()
        }
    }
}

@Composable
private fun PlayerErrorEffect(
    playerState: PlayerState?,
    snackbarHostState: SnackbarHostState,
    onRetry: () -> Unit,
) {
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
            onRetry()
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
                onPlayPauseClick = state.player::togglePlayPause,
            )
        }
    }
}

private fun List<RadioStation>.toMediaItems() = map { it.toMediaItem() }
