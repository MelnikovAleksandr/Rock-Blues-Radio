package ru.asmelnikov.rockbluesradio.playback

import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import ru.asmelnikov.rockbluesradio.testing.createTestPlayer
import ru.asmelnikov.rockbluesradio.testing.onMainThread
import ru.asmelnikov.rockbluesradio.testing.releaseOnMainThread

@RunWith(AndroidJUnit4::class)
class PlaylistExtensionsTest {

    private val player: ExoPlayer = createTestPlayer()

    @After
    fun tearDown() {
        player.releaseOnMainThread()
    }

    private fun items(vararg ids: String) = ids.map {
        MediaItem.Builder().setMediaId(it).setUri("https://stream.example/$it").build()
    }

    private fun mediaIds() = onMainThread { List(player.mediaItemCount) { player.getMediaItemAt(it).mediaId } }

    private fun currentMediaId() = onMainThread { player.currentMediaItem?.mediaId }

    @Test
    fun sameMediaIdsAreDetected() = onMainThread {
        player.setMediaItems(items("a", "b"))

        assertTrue(player.hasSameMediaIds(items("a", "b")))
        assertFalse(player.hasSameMediaIds(items("b", "a")))
        assertFalse(player.hasSameMediaIds(items("a")))
    }

    @Test
    fun queueIsReplacedAroundCurrentItem() {
        onMainThread {
            player.setMediaItems(items("a", "b", "c", "d"), 2, 0)
            player.replaceQueueAroundCurrentItem(items("x", "c", "y"))
        }

        assertEquals(listOf("x", "c", "y"), mediaIds())
        assertEquals("c", currentMediaId())
        assertEquals(1, onMainThread { player.currentMediaItemIndex })
    }

    @Test
    fun currentItemIsKeptWhenItLeavesTheQueue() {
        onMainThread {
            player.setMediaItems(items("a", "b", "c"), 1, 0)
            player.replaceQueueAroundCurrentItem(items("a", "c"))
        }

        assertEquals(listOf("b", "a", "c"), mediaIds())
        assertEquals("b", currentMediaId())
    }

    @Test
    fun emptyPlayerIsLeftUntouched() {
        onMainThread { player.replaceQueueAroundCurrentItem(items("a")) }

        assertTrue(mediaIds().isEmpty())
    }
}
