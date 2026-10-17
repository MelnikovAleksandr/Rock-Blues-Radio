package ru.asmelnikov.rockbluesradio.testing

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import androidx.test.platform.app.InstrumentationRegistry

val targetContext: Context
    get() = InstrumentationRegistry.getInstrumentation().targetContext

fun <T> onMainThread(block: () -> T): T {
    var result: Result<T>? = null
    InstrumentationRegistry.getInstrumentation().runOnMainSync { result = runCatching(block) }
    return result!!.getOrThrow()
}

fun createTestPlayer(): ExoPlayer = onMainThread { ExoPlayer.Builder(targetContext).build() }

fun ExoPlayer.releaseOnMainThread() = onMainThread { release() }
