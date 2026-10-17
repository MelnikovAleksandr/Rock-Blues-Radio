package ru.asmelnikov.rockbluesradio.presentation.player

import android.content.ComponentName
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import ru.asmelnikov.rockbluesradio.playback.PlaybackService

@Composable
fun rememberManagedMediaController(
    lifecycle: Lifecycle = LocalLifecycleOwner.current.lifecycle,
): State<MediaController?> {
    val appContext = LocalContext.current.applicationContext
    val controllerManager = remember { MediaControllerManager.getInstance(appContext) }

    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> controllerManager.initialize()
                Lifecycle.Event.ON_STOP -> controllerManager.release()
                else -> Unit
            }
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }

    return controllerManager.controller
}

@Stable
internal class MediaControllerManager private constructor(context: Context) : RememberObserver {

    private val appContext = context.applicationContext
    private var controllerFuture: ListenableFuture<MediaController>? = null

    private val _controller = mutableStateOf<MediaController?>(null)
    val controller: State<MediaController?> = _controller

    init {
        initialize()
    }

    fun initialize() {
        if (controllerFuture == null || controllerFuture?.isDone == true) {
            val sessionToken = SessionToken(appContext, ComponentName(appContext, PlaybackService::class.java))
            controllerFuture = MediaController.Builder(appContext, sessionToken).buildAsync()
        }
        controllerFuture?.addListener(
            { _controller.value = controllerFuture?.let { if (it.isDone) it.get() else null } },
            MoreExecutors.directExecutor(),
        )
    }

    fun release() {
        controllerFuture?.let {
            MediaController.releaseFuture(it)
            _controller.value = null
        }
        controllerFuture = null
    }

    override fun onRemembered() = Unit

    override fun onForgotten() {
        release()
    }

    override fun onAbandoned() {
        release()
    }

    companion object {
        @Volatile
        private var instance: MediaControllerManager? = null

        fun getInstance(context: Context): MediaControllerManager = instance ?: synchronized(this) {
            instance ?: MediaControllerManager(context).also { instance = it }
        }
    }
}
