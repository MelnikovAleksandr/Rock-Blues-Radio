package ru.asmelnikov.rockbluesradio.presentation.components.player

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.chrisbanes.haze.rememberHazeState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.asmelnikov.rockbluesradio.R
import ru.asmelnikov.rockbluesradio.presentation.theme.RockBluesRadioTheme
import ru.asmelnikov.rockbluesradio.testing.targetContext

@RunWith(AndroidJUnit4::class)
class MiniPlayerTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun mediaItem(title: String?) = MediaItem.Builder()
        .setMediaId("rock_1")
        .setMediaMetadata(MediaMetadata.Builder().setDisplayTitle(title).build())
        .build()

    @Test
    fun showsStationTitle() {
        setContent(mediaItem("Classic Rock FM"))

        composeRule.onNodeWithText("Classic Rock FM").assertIsDisplayed()
    }

    @Test
    fun missingTitleIsNotRenderedAsNull() {
        setContent(mediaItem(null))

        composeRule.onNodeWithText("null").assertDoesNotExist()
    }

    @Test
    fun playPauseButtonFollowsPlaybackAndReportsClicks() {
        var isPlaying by mutableStateOf(false)
        var clicks = 0
        composeRule.setContent {
            RockBluesRadioTheme {
                MiniPlayer(
                    mediaItem = mediaItem("Station"),
                    isPlaying = isPlaying,
                    isBuffering = false,
                    hazeState = rememberHazeState(),
                    onPlayPauseClick = { clicks++ },
                )
            }
        }

        composeRule.onNodeWithContentDescription(targetContext.getString(R.string.play)).performClick()
        isPlaying = true

        composeRule.onNodeWithContentDescription(targetContext.getString(R.string.pause)).assertIsDisplayed()
        assertEquals(1, clicks)
    }

    private fun setContent(mediaItem: MediaItem) {
        composeRule.setContent {
            RockBluesRadioTheme {
                MiniPlayer(
                    mediaItem = mediaItem,
                    isPlaying = false,
                    isBuffering = false,
                    hazeState = rememberHazeState(),
                    onPlayPauseClick = {},
                )
            }
        }
    }
}
