package ru.asmelnikov.rockbluesradio.presentation.components.player.expanded

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.media3.exoplayer.ExoPlayer
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.asmelnikov.rockbluesradio.R
import ru.asmelnikov.rockbluesradio.playback.StationFolder
import ru.asmelnikov.rockbluesradio.playback.toMediaItems
import ru.asmelnikov.rockbluesradio.presentation.player.PlayerState
import ru.asmelnikov.rockbluesradio.presentation.theme.RockBluesRadioTheme
import ru.asmelnikov.rockbluesradio.testing.createTestPlayer
import ru.asmelnikov.rockbluesradio.testing.onMainThread
import ru.asmelnikov.rockbluesradio.testing.releaseOnMainThread
import ru.asmelnikov.rockbluesradio.testing.targetContext
import ru.asmelnikov.rockbluesradio.testing.testStations

@RunWith(AndroidJUnit4::class)
class ExpandedPlayerTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val stations = testStations("rock", 8)
    private lateinit var player: ExoPlayer
    private lateinit var playerState: PlayerState
    private var collapsed = false

    @Before
    fun setUp() {
        player = createTestPlayer()
        playerState = onMainThread {
            player.setMediaItems(stations.toMediaItems(StationFolder.Rock))
            PlayerState(player)
        }
        composeRule.setContent {
            RockBluesRadioTheme {
                ExpandedPlayer(playerState = playerState, onCollapse = { collapsed = true })
            }
        }
    }

    @After
    fun tearDown() {
        onMainThread { playerState.dispose() }
        player.releaseOnMainThread()
    }

    private fun playerIndex() = onMainThread { player.currentMediaItemIndex }

    @Test
    fun showsCurrentStationTitle() {
        composeRule.onNodeWithText(stations[0].name).assertIsDisplayed()
    }

    @Test
    fun nextAndPreviousSwitchStations() {
        composeRule.onNodeWithContentDescription(targetContext.getString(R.string.next_station)).performClick()
        composeRule.waitForIdle()
        assertEquals(1, playerIndex())
        composeRule.onNodeWithText(stations[1].name).assertIsDisplayed()

        composeRule.onNodeWithContentDescription(targetContext.getString(R.string.previous_station)).performClick()
        composeRule.waitForIdle()
        assertEquals(0, playerIndex())
    }

    @Test
    fun jumpOfThePlayerDoesNotMakeStationsSwitchByThemselves() {
        onMainThread { player.seekToDefaultPosition(6) }
        composeRule.waitForIdle()

        assertEquals(6, playerIndex())
        composeRule.onNodeWithText(stations[6].name).assertIsDisplayed()
    }

    @Test
    fun wrapAroundDoesNotMakeStationsSwitchByThemselves() {
        onMainThread { player.seekToDefaultPosition(stations.lastIndex) }
        composeRule.waitForIdle()
        onMainThread { player.seekToDefaultPosition(0) }
        composeRule.waitForIdle()

        assertEquals(0, playerIndex())
    }

    @Test
    fun playPauseTogglesPlayback() {
        composeRule.onNodeWithContentDescription(targetContext.getString(R.string.play)).performClick()
        composeRule.waitForIdle()

        assertEquals(true, onMainThread { player.playWhenReady })
    }

    @Test
    fun collapseButtonCollapsesThePlayer() {
        composeRule.onNodeWithContentDescription(targetContext.getString(R.string.collapse_player)).performClick()

        assertEquals(true, collapsed)
    }
}
