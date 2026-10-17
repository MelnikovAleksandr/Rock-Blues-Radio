package ru.asmelnikov.rockbluesradio.testing

import android.app.ActivityManager
import android.content.ComponentName
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.TimeUnit
import ru.asmelnikov.rockbluesradio.playback.PlaybackService

const val SERVICE_TIMEOUT_SECONDS = 10L
private const val POLL_INTERVAL_MILLIS = 50L

fun <T> ListenableFuture<T>.await(): T = get(SERVICE_TIMEOUT_SECONDS, TimeUnit.SECONDS)

fun connectMediaBrowser(): MediaBrowser {
    val token = SessionToken(targetContext, ComponentName(targetContext, PlaybackService::class.java))
    return onMainThread { MediaBrowser.Builder(targetContext, token).buildAsync() }.await()
}

@Suppress("DEPRECATION")
fun isPlaybackServiceRunning(): Boolean = targetContext.getSystemService(ActivityManager::class.java)
    .getRunningServices(Int.MAX_VALUE)
    .any { it.service.className == PlaybackService::class.java.name }

fun awaitPlaybackServiceStopped() {
    waitUntil { !isPlaybackServiceRunning() }
}

fun waitUntil(condition: () -> Boolean): Boolean {
    val deadline = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(SERVICE_TIMEOUT_SECONDS)
    while (System.currentTimeMillis() < deadline) {
        if (condition()) return true
        Thread.sleep(POLL_INTERVAL_MILLIS)
    }
    return condition()
}
