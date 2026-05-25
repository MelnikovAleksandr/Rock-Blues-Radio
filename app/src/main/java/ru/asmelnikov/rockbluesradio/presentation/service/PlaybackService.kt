package ru.asmelnikov.rockbluesradio.presentation.service

import android.app.PendingIntent
import android.content.Intent
import androidx.annotation.OptIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.core.app.TaskStackBuilder
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import ru.asmelnikov.rockbluesradio.presentation.MainActivity

@ExperimentalMaterial3Api
@OptIn(UnstableApi::class)
class PlaybackService : MediaSessionService() {
    private var _mediaSession: MediaSession? = null

    companion object {
        private const val FLAG = PendingIntent.FLAG_IMMUTABLE
    }

    override fun onCreate() {
        super.onCreate()
        val player = ExoPlayer.Builder(this).build()
        _mediaSession = MediaSession.Builder(this, player)
            .apply {
                getSingleTopActivity()?.let { setSessionActivity(it) }
            }
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return _mediaSession
    }

    override fun onDestroy() {
        _mediaSession?.run {
            player.release()
            release()
            _mediaSession = null
        }
        super.onDestroy()
    }

    private fun getSingleTopActivity(): PendingIntent? {
        return PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            FLAG or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}