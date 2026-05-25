package ru.asmelnikov.rockbluesradio.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.rememberNavBackStack
import kotlinx.coroutines.launch
import ru.asmelnikov.rockbluesradio.data.model.toMediaItem
import ru.asmelnikov.rockbluesradio.presentation.components.CompactPlayerView
import ru.asmelnikov.rockbluesradio.presentation.components.ExpandedPlayerView
import ru.asmelnikov.rockbluesradio.presentation.navigation.NavGraph
import ru.asmelnikov.rockbluesradio.presentation.navigation.Routes
import ru.asmelnikov.rockbluesradio.presentation.player.PlayerState
import ru.asmelnikov.rockbluesradio.presentation.player.playMediaAt
import ru.asmelnikov.rockbluesradio.presentation.player.state
import ru.asmelnikov.rockbluesradio.presentation.player.updatePlaylist
import ru.asmelnikov.rockbluesradio.presentation.service.rememberManagedMediaController
import ru.asmelnikov.rockbluesradio.presentation.theme.RockBluesRadioTheme
import kotlin.getValue

@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RockBluesRadioTheme {
                val navController = rememberNavBackStack(Routes.MainScreen)
                val snackbarHostState = remember { SnackbarHostState() }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = {
                        SnackbarHost(snackbarHostState)
                    }
                ) { innerPadding ->

                    val isPlayerSetUp by mainViewModel.isPlayerSetUp.collectAsStateWithLifecycle()
                    val mediaController by rememberManagedMediaController()
                    var playerState: PlayerState? by remember {
                        mutableStateOf(mediaController?.state())
                    }
                    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                    val coroutineScope = rememberCoroutineScope()
                    var openBottomSheet by remember { mutableStateOf(false) }

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

                    LaunchedEffect(key1 = playerState?.playerError) {
                        playerState?.playerError?.let { exception ->
                            val result = snackbarHostState.showSnackbar(
                                message = "${exception.message}, Code: ${exception.errorCode}",
                                withDismissAction = true,
                                actionLabel = "Retry"
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                mediaController?.prepare()
                            }
                        }
                    }

                    if (openBottomSheet && playerState != null) {
                        ModalBottomSheet(
                            onDismissRequest = {
                                openBottomSheet = false
                            },
                            shape = RectangleShape,
                            sheetState = sheetState,
                        ) {
                            ExpandedPlayerView(
                                modifier = Modifier,
                                playerState = playerState!!,
                                onCollapseTap = {
                                    coroutineScope.launch {
                                        sheetState.hide()
                                        openBottomSheet = false
                                    }
                                },
                                onMenuTap = {},
                                onPrevClick = {
                                    mediaController?.seekToPreviousMediaItem()
                                },
                                onNextClick = {
                                    mediaController?.seekToNextMediaItem()
                                }
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        NavGraph(
                            backStack = navController,
                            paddingValues = innerPadding,
                            onNextPage = {
                                mediaController?.updatePlaylist(it.map { item -> item.toMediaItem() })
                            },
                            onRadioStationClick = { index ->
                                mainViewModel.setupPlayer()
                                mediaController?.playMediaAt(index)
                            },
                            isPlayerSetUp = isPlayerSetUp,
                            onFavoriteItemClick = { items, index ->
                                mediaController?.updatePlaylist(items.map { item -> item.toMediaItem() })
                                mainViewModel.setupPlayer()
                                mediaController?.playMediaAt(index)
                            }
                        )

                        if (isPlayerSetUp && playerState != null) {
                            CompactPlayerView(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp)
                                    .align(Alignment.BottomCenter)
                                    .clickable {
                                        coroutineScope.launch {
                                            sheetState.expand()
                                            openBottomSheet = true
                                        }
                                    },
                                playerState = playerState!!
                            )
                        }
                    }
                }
            }
        }
    }
}