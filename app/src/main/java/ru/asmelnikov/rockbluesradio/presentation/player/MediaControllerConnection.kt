package ru.asmelnikov.rockbluesradio.presentation.player

import android.content.ComponentName
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import ru.asmelnikov.rockbluesradio.playback.PlaybackService

class MediaControllerConnection(
    context: Context,
    private val onConnected: (MediaController) -> Unit,
    private val onDisconnected: () -> Unit,
) {
    private val appContext = context.applicationContext
    private val sessionToken = SessionToken(appContext, ComponentName(appContext, PlaybackService::class.java))
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var isActive = false

    fun connect() {
        isActive = true
        if (controllerFuture != null) return

        lateinit var future: ListenableFuture<MediaController>
        future = MediaController.Builder(appContext, sessionToken)
            .setListener(
                object : MediaController.Listener {
                    override fun onDisconnected(controller: MediaController) {
                        onSessionDisconnected(future)
                    }
                },
            )
            .buildAsync()
        controllerFuture = future
        future.addListener(
            {
                if (controllerFuture !== future) return@addListener
                runCatching { future.get() }
                    .onSuccess(onConnected)
                    .onFailure { releaseController() }
            },
            ContextCompat.getMainExecutor(appContext),
        )
    }

    fun disconnect() {
        isActive = false
        releaseController()
    }

    private fun onSessionDisconnected(future: ListenableFuture<MediaController>) {
        if (controllerFuture !== future) return
        releaseController()
        if (isActive) connect()
    }

    private fun releaseController() {
        val future = controllerFuture ?: return
        controllerFuture = null
        MediaController.releaseFuture(future)
        onDisconnected()
    }
}

@Composable
fun rememberMediaController(): MediaController? {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var controller by remember { mutableStateOf<MediaController?>(null) }
    val connection = remember {
        MediaControllerConnection(
            context = context,
            onConnected = { controller = it },
            onDisconnected = { controller = null },
        )
    }

    DisposableEffect(lifecycleOwner, connection) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> connection.connect()
                Lifecycle.Event.ON_STOP -> connection.disconnect()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            connection.disconnect()
        }
    }

    return controller
}
