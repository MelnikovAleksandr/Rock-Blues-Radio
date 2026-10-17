package ru.asmelnikov.rockbluesradio.playback

import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.SettableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun <T> CoroutineScope.launchFuture(block: suspend () -> T): ListenableFuture<T> {
    val future = SettableFuture.create<T>()
    launch {
        runCatching { block() }
            .onSuccess { future.set(it) }
            .onFailure { future.setException(it) }
    }
    return future
}
