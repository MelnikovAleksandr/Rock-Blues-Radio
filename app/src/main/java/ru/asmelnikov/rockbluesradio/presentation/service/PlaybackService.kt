package ru.asmelnikov.rockbluesradio.presentation.service

import android.app.PendingIntent
import android.content.Intent
import androidx.annotation.OptIn
import androidx.core.app.TaskStackBuilder
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import ru.asmelnikov.rockbluesradio.presentation.MainActivity

@OptIn(UnstableApi::class)
class PlaybackService : MediaSessionService() {
    private var _mediaSession: MediaSession? = null
    private val mediaSession get() = _mediaSession!!

    companion object {
        private const val FLAG = PendingIntent.FLAG_IMMUTABLE
    }


    override fun onCreate() {
        super.onCreate()

        val player = ExoPlayer.Builder(this).build()
        _mediaSession = MediaSession.Builder(this, player)
            .also { builder ->
                getSingleTopActivity()?.let { builder.setSessionActivity(it) }
            }
            .build()
    }


    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession.player
        if (!player.playWhenReady || player.mediaItemCount == 0) {
            stopSelf()
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return _mediaSession
    }

    override fun onDestroy() {
        _mediaSession?.run {
            getBackStackedActivity()?.let { setSessionActivity(it) }
            player.release()
            release()
            _mediaSession = null
        }
        clearListener()
        super.onDestroy()
    }

    private fun getSingleTopActivity(): PendingIntent? {
        return PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            FLAG or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun getBackStackedActivity(): PendingIntent? {
        return TaskStackBuilder.create(this).run {
            addNextIntent(Intent(this@PlaybackService, MainActivity::class.java))
            getPendingIntent(0, FLAG or PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }
}