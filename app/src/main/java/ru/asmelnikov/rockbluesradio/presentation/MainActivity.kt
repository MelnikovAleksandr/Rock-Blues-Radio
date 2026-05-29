package ru.asmelnikov.rockbluesradio.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import androidx.navigation3.runtime.rememberNavBackStack
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch
import ru.asmelnikov.rockbluesradio.R
import ru.asmelnikov.rockbluesradio.domain.model.toMediaItem
import ru.asmelnikov.rockbluesradio.presentation.components.CompactPlayerView
import ru.asmelnikov.rockbluesradio.presentation.components.NoInternetDialog
import ru.asmelnikov.rockbluesradio.presentation.components.expanded.ExpandedPlayerView
import ru.asmelnikov.rockbluesradio.presentation.navigation.NavGraph
import ru.asmelnikov.rockbluesradio.presentation.navigation.Routes
import ru.asmelnikov.rockbluesradio.presentation.player.PlayerState
import ru.asmelnikov.rockbluesradio.presentation.player.isBuffering
import ru.asmelnikov.rockbluesradio.presentation.player.playMediaAt
import ru.asmelnikov.rockbluesradio.presentation.player.state
import ru.asmelnikov.rockbluesradio.presentation.player.updatePlaylist
import ru.asmelnikov.rockbluesradio.presentation.service.rememberManagedMediaController
import ru.asmelnikov.rockbluesradio.presentation.theme.RockBluesRadioTheme
import ru.asmelnikov.rockbluesradio.presentation.theme.dimens
import ru.asmelnikov.rockbluesradio.presentation.utils.NetworkMonitor

@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        installSplashScreen()
        setContent {
            RockBluesRadioTheme {
                val context = LocalContext.current
                val navController = rememberNavBackStack(Routes.MainScreen)
                val snackbarHostState = remember { SnackbarHostState() }
                val isPlayerSetUp by mainViewModel.isPlayerSetUp.collectAsStateWithLifecycle()
                val mediaController by rememberManagedMediaController()
                var playerState: PlayerState? by remember { mutableStateOf(mediaController?.state()) }
                val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                val coroutineScope = rememberCoroutineScope()
                var openBottomSheet by rememberSaveable { mutableStateOf(false) }
                val hazeState = rememberHazeState()
                val errorMessage = stringResource(R.string.error)
                val retryMessage = stringResource(R.string.error_retry)
                val networkMonitor = remember { NetworkMonitor(context) }
                val isConnected by networkMonitor.isConnected.collectAsState()
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
                    snackbarHost = {
                        SnackbarHost(
                            hostState = snackbarHostState,
                            modifier = Modifier.navigationBarsPadding()
                        )
                    }
                ) { innerPadding ->

                    LaunchedEffect(mediaController) {
                        mediaController?.let { controller ->
                            if (controller.playWhenReady || controller.playbackState == Player.STATE_READY) {
                                mainViewModel.setupPlayer()
                            }
                        }
                    }

                    LaunchedEffect(key1 = isPlayerSetUp) {
                        if (isPlayerSetUp) {
                            mediaController?.run {
                                if (mediaItemCount > 0) {
                                    prepare()
                                    play()
                                }
                            }
                        }
                    }

                    DisposableEffect(key1 = mediaController) {
                        mediaController?.run {
                            playerState = state()
                        }
                        onDispose {
                            playerState?.dispose()
                        }
                    }

                    DisposableEffect(Unit) {
                        onDispose {
                            networkMonitor.unregister()
                        }
                    }

                    LaunchedEffect(key1 = playerState?.playerError) {
                        playerState?.playerError?.let { exception ->
                            val result = snackbarHostState.showSnackbar(
                                message = "$errorMessage ${exception.message}, Code: ${exception.errorCode}",
                                withDismissAction = true,
                                actionLabel = retryMessage
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                mediaController?.prepare()
                            }
                        }
                    }

                    if (!isConnected) {
                        NoInternetDialog()
                    }

                    if (openBottomSheet) {
                        ModalBottomSheet(
                            onDismissRequest = {
                                coroutineScope.launch {
                                    sheetState.hide()
                                }.invokeOnCompletion {
                                    if (!sheetState.isVisible) {
                                        openBottomSheet = false
                                    }
                                }
                            },
                            shape = RectangleShape,
                            sheetState = sheetState,
                            contentWindowInsets = { WindowInsets(0) },
                            dragHandle = null,
                            sheetMaxWidth = Dp.Unspecified
                        ) {
                            playerState?.let {
                                ExpandedPlayerView(
                                    modifier = Modifier,
                                    playerState = it,
                                    onCollapseTap = {
                                        coroutineScope.launch {
                                            sheetState.hide()
                                        }.invokeOnCompletion {
                                            if (!sheetState.isVisible) {
                                                openBottomSheet = false
                                            }
                                        }
                                    },
                                    onPrevClick = {
                                        mediaController?.seekToPreviousMediaItem()
                                    },
                                    onNextClick = {
                                        mediaController?.seekToNextMediaItem()
                                    }
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        NavGraph(
                            backStack = navController,
                            onItemsUpdate = {
                                mediaController?.updatePlaylist(it.map { item -> item.toMediaItem() })
                            },
                            onRadioStationClick = { index ->
                                if (!isPlayerSetUp) {
                                    mainViewModel.setupPlayer()
                                }
                                mediaController?.playMediaAt(index)
                            },
                            isPlayerSetUp = isPlayerSetUp,
                            hazeState = hazeState,
                            currentPlayingStationId = playerState?.takeIf { it.isPlaying }?.currentMediaItem?.mediaId
                                ?: ""
                        )
                        AnimatedVisibility(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(dimens.extraSmall2)
                                .navigationBarsPadding(),
                            visible = isPlayerSetUp && playerState != null && !openBottomSheet && playerState?.currentMediaItem != null,
                            enter = slideInVertically(
                                initialOffsetY = { it },
                                animationSpec = tween(durationMillis = 300)
                            ) + fadeIn(),
                            exit = slideOutVertically(
                                targetOffsetY = { it },
                                animationSpec = tween(durationMillis = 300)
                            ) + fadeOut()
                        ) {
                            playerState?.let { safeState ->
                                CompactPlayerView(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            openBottomSheet = true
                                        },
                                    currentMediaItem = safeState.currentMediaItem,
                                    isBuffering = safeState.isBuffering,
                                    isPlaying = safeState.isPlaying,
                                    hazeState = hazeState,
                                    onPlayPauseClick = {
                                        if (safeState.player.playbackState == Player.STATE_IDLE && safeState.currentMediaItem != null) {
                                            safeState.player.prepare()
                                            safeState.player.play()
                                        } else {
                                            safeState.player.playWhenReady =
                                                !safeState.player.playWhenReady
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}