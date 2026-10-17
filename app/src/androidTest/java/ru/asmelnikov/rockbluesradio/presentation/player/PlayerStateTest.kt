package ru.asmelnikov.rockbluesradio.presentation.player

import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import ru.asmelnikov.rockbluesradio.playback.PlaybackContract
import ru.asmelnikov.rockbluesradio.playback.StationFolder
import ru.asmelnikov.rockbluesradio.playback.toMediaItems
import ru.asmelnikov.rockbluesradio.testing.createTestPlayer
import ru.asmelnikov.rockbluesradio.testing.onMainThread
import ru.asmelnikov.rockbluesradio.testing.releaseOnMainThread
import ru.asmelnikov.rockbluesradio.testing.testStations

@RunWith(AndroidJUnit4::class)
class PlayerStateTest {

    private val rock = testStations("rock", 4)
    private val blues = testStations("blues", 3)
    private val player: ExoPlayer = createTestPlayer()
    private val state = onMainThread { PlayerState(player) }

    @After
    fun tearDown() {
        onMainThread { state.dispose() }
        player.releaseOnMainThread()
    }

    private fun currentMediaId() = onMainThread { player.currentMediaItem?.mediaId }

    @Test
    fun playStationStartsTheSelectedStation() {
        onMainThread { state.playStation(StationFolder.Rock, rock, 2) }

        assertEquals(PlaybackContract.stationMediaId(StationFolder.Rock, rock[2].id), currentMediaId())
        assertEquals(rock.size, onMainThread { player.mediaItemCount })
        assertTrue(onMainThread { player.playWhenReady })
    }

    @Test
    fun stationAtSameIndexInAnotherListStartsPlayingInsteadOfPausing() {
        onMainThread { state.playStation(StationFolder.Rock, rock, 0) }
        onMainThread { state.playStation(StationFolder.Blues, blues, 0) }

        assertEquals(PlaybackContract.stationMediaId(StationFolder.Blues, blues[0].id), currentMediaId())
        assertEquals(blues.size, onMainThread { player.mediaItemCount })
        assertTrue(onMainThread { player.playWhenReady })
    }

    @Test
    fun tappingTheCurrentStationTogglesPlayback() {
        onMainThread { state.playStation(StationFolder.Rock, rock, 1) }
        onMainThread { state.playStation(StationFolder.Rock, rock, 1) }

        assertFalse(onMainThread { player.playWhenReady })

        onMainThread { state.playStation(StationFolder.Rock, rock, 1) }
        assertTrue(onMainThread { player.playWhenReady })
    }

    @Test
    fun anotherStationOfTheSameListKeepsThePlaylist() {
        onMainThread { state.playStation(StationFolder.Rock, rock, 0) }
        val firstItem = onMainThread { player.getMediaItemAt(3) }

        onMainThread { state.playStation(StationFolder.Rock, rock, 3) }

        assertEquals(3, onMainThread { player.currentMediaItemIndex })
        assertTrue(firstItem === onMainThread { player.getMediaItemAt(3) })
    }

    @Test
    fun invalidIndexIsIgnored() {
        onMainThread { state.playStation(StationFolder.Rock, rock, rock.size) }

        assertNull(currentMediaId())
    }

    @Test
    fun togglePlayPausePreparesAnIdlePlayer() {
        onMainThread { player.setMediaItems(rock.toMediaItems(StationFolder.Rock)) }

        onMainThread { state.togglePlayPause() }

        assertNotEquals(Player.STATE_IDLE, onMainThread { player.playbackState })
        assertTrue(onMainThread { player.playWhenReady })
    }

    @Test
    fun skipMovesBetweenStations() {
        onMainThread { player.setMediaItems(rock.toMediaItems(StationFolder.Rock)) }

        onMainThread { state.skipToNext() }
        assertEquals(1, onMainThread { player.currentMediaItemIndex })

        onMainThread { state.skipToPrevious() }
        assertEquals(0, onMainThread { player.currentMediaItemIndex })

        onMainThread { state.skipTo(3) }
        assertEquals(3, onMainThread { player.currentMediaItemIndex })

        onMainThread { state.skipTo(rock.size) }
        assertEquals(3, onMainThread { player.currentMediaItemIndex })
    }

    @Test
    fun stateFollowsThePlayer() {
        onMainThread { state.playStation(StationFolder.Blues, blues, 1) }

        onMainThread {
            assertEquals(
                blues.toMediaItems(StationFolder.Blues).map {
                    it.mediaId
                },
                state.mediaItems.map { it.mediaId },
            )
            assertEquals(1, state.mediaItemIndex)
            assertEquals(
                PlaybackContract.stationMediaId(StationFolder.Blues, blues[1].id),
                state.currentMediaItem?.mediaId,
            )
        }
    }
}
