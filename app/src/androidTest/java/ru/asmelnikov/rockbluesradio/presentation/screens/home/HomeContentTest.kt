package ru.asmelnikov.rockbluesradio.presentation.screens.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.IdlingPolicies
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.chrisbanes.haze.rememberHazeState
import java.util.concurrent.TimeUnit
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.asmelnikov.rockbluesradio.R
import ru.asmelnikov.rockbluesradio.domain.model.Genre
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation
import ru.asmelnikov.rockbluesradio.presentation.player.NowPlaying
import ru.asmelnikov.rockbluesradio.presentation.theme.RockBluesRadioTheme
import ru.asmelnikov.rockbluesradio.testing.targetContext
import ru.asmelnikov.rockbluesradio.testing.testStation
import ru.asmelnikov.rockbluesradio.testing.testStations

@RunWith(AndroidJUnit4::class)
class HomeContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val rock = testStations("rock", 3)
    private val blues = testStations("blues", 2)

    @After
    fun resetIdlingTimeouts() {
        IdlingPolicies.setIdlingResourceTimeout(DEFAULT_IDLING_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        IdlingPolicies.setMasterPolicyTimeout(DEFAULT_MASTER_TIMEOUT_SECONDS, TimeUnit.SECONDS)
    }

    private var clickedStation: Triple<Genre, List<RadioStation>, Int>? = null
    private var favoriteClicked: RadioStation? = null
    private var favoritesOpened = false

    private fun setContent(state: HomeUiState = HomeUiState(mapOf(Genre.Rock to rock, Genre.Blues to blues))) {
        composeRule.setContent {
            RockBluesRadioTheme {
                HomeContent(
                    state = state,
                    nowPlaying = NowPlaying(),
                    hazeState = rememberHazeState(),
                    onStationClick = { genre, stations, index -> clickedStation = Triple(genre, stations, index) },
                    onFavoritesClick = { favoritesOpened = true },
                    onFavoriteClick = { favoriteClicked = it },
                )
            }
        }
    }

    @Test
    fun rockStationsAreShownFirst() {
        setContent()

        composeRule.onNodeWithText(rock[0].name).assertIsDisplayed()
        composeRule.onNodeWithText(blues[0].name).assertDoesNotExist()
    }

    @Test
    fun genreSwitcherOpensBluesStations() {
        setContent()
        composeRule.mainClock.autoAdvance = false

        composeRule.onNodeWithText(targetContext.getString(R.string.blues)).performClick()
        composeRule.mainClock.advanceTimeBy(PAGE_SWITCH_MILLIS)

        composeRule.onNodeWithText(blues[0].name).assertIsDisplayed()
        composeRule.onNodeWithText(rock[0].name).assertDoesNotExist()
    }

    @Test
    fun screenSettlesAfterSwitchingGenre() {
        setContent()
        IdlingPolicies.setIdlingResourceTimeout(SETTLE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        IdlingPolicies.setMasterPolicyTimeout(SETTLE_TIMEOUT_SECONDS, TimeUnit.SECONDS)

        composeRule.onNodeWithText(targetContext.getString(R.string.blues)).performClick()

        composeRule.waitForIdle()
        composeRule.onNodeWithText(blues[0].name).assertIsDisplayed()
    }

    @Test
    fun stationClickReportsGenreListAndIndex() {
        setContent()

        composeRule.onNodeWithText(rock[2].name).performClick()

        assertEquals(Triple(Genre.Rock, rock, 2), clickedStation)
    }

    @Test
    fun favoriteButtonReportsItsStation() {
        setContent()

        composeRule.onAllNodesWithContentDescription(targetContext.getString(R.string.add_to_favorites))[1]
            .performClick()

        assertEquals(rock[1], favoriteClicked)
    }

    @Test
    fun favoriteStationOffersRemoval() {
        setContent(HomeUiState(mapOf(Genre.Rock to listOf(testStation("fav", isFavorite = true)))))

        composeRule.onNodeWithContentDescription(targetContext.getString(R.string.remove_from_favorites))
            .assertIsDisplayed()
    }

    @Test
    fun favoritesButtonIsHiddenWithoutFavorites() {
        setContent()

        composeRule.onNodeWithContentDescription(targetContext.getString(R.string.favorite_stations))
            .assertDoesNotExist()
    }

    @Test
    fun favoritesButtonOpensFavorites() {
        setContent(HomeUiState(mapOf(Genre.Rock to rock), showFavoritesButton = true))

        composeRule.onNodeWithContentDescription(targetContext.getString(R.string.favorite_stations)).performClick()

        assertEquals(true, favoritesOpened)
    }

    private companion object {
        const val PAGE_SWITCH_MILLIS = 2_000L
        const val SETTLE_TIMEOUT_SECONDS = 5L
        const val DEFAULT_IDLING_TIMEOUT_SECONDS = 26L
        const val DEFAULT_MASTER_TIMEOUT_SECONDS = 60L
    }
}
