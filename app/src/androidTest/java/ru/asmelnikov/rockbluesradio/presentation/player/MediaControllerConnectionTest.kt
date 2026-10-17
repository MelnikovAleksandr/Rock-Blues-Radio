package ru.asmelnikov.rockbluesradio.presentation.player

import androidx.media3.session.MediaController
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import ru.asmelnikov.rockbluesradio.testing.awaitPlaybackServiceStopped
import ru.asmelnikov.rockbluesradio.testing.onMainThread
import ru.asmelnikov.rockbluesradio.testing.targetContext
import ru.asmelnikov.rockbluesradio.testing.waitUntil

@RunWith(AndroidJUnit4::class)
class MediaControllerConnectionTest {

    @Volatile
    private var controller: MediaController? = null
    private lateinit var connection: MediaControllerConnection

    @Before
    fun setUp() {
        awaitPlaybackServiceStopped()
        connection = onMainThread {
            MediaControllerConnection(
                context = targetContext,
                onConnected = { controller = it },
                onDisconnected = { controller = null },
            )
        }
    }

    @After
    fun tearDown() {
        onMainThread { connection.disconnect() }
        awaitPlaybackServiceStopped()
    }

    @Test
    fun connectProvidesController() {
        onMainThread { connection.connect() }

        assertTrue(waitUntil { controller?.isConnected == true })
    }

    @Test
    fun disconnectReleasesController() {
        onMainThread { connection.connect() }
        waitUntil { controller != null }

        onMainThread { connection.disconnect() }

        assertNull(controller)
    }

    @Test
    fun immediateReconnectAfterDisconnectStillConnects() {
        onMainThread { connection.connect() }
        waitUntil { controller != null }

        onMainThread {
            connection.disconnect()
            connection.connect()
        }

        assertTrue(waitUntil { controller?.isConnected == true })
    }

    @Test
    fun repeatedConnectCallsKeepOneController() {
        onMainThread {
            connection.connect()
            connection.connect()
        }
        waitUntil { controller != null }
        val connected = controller

        onMainThread { connection.connect() }

        assertTrue(connected === controller)
    }
}
