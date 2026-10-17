package ru.asmelnikov.rockbluesradio.presentation.screens.favorites

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.chrisbanes.haze.rememberHazeState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.asmelnikov.rockbluesradio.R
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation
import ru.asmelnikov.rockbluesradio.domain.usecase.ObserveFavoriteStationsUseCase
import ru.asmelnikov.rockbluesradio.domain.usecase.ToggleFavoriteUseCase
import ru.asmelnikov.rockbluesradio.presentation.player.NowPlaying
import ru.asmelnikov.rockbluesradio.presentation.theme.RockBluesRadioTheme
import ru.asmelnikov.rockbluesradio.testing.FakeRadioStationRepository
import ru.asmelnikov.rockbluesradio.testing.targetContext
import ru.asmelnikov.rockbluesradio.testing.testStations

@RunWith(AndroidJUnit4::class)
class FavoritesScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val favorites = testStations("fav", 3)
    private var clickedStation: Pair<List<RadioStation>, Int>? = null
    private var backCount = 0

    private fun setContent(repository: FakeRadioStationRepository) {
        val viewModel =
            FavoritesViewModel(ObserveFavoriteStationsUseCase(repository), ToggleFavoriteUseCase(repository))
        composeRule.setContent {
            RockBluesRadioTheme {
                FavoritesScreen(
                    nowPlaying = NowPlaying(),
                    hazeState = rememberHazeState(),
                    onStationClick = { stations, index -> clickedStation = stations to index },
                    onBack = { backCount++ },
                    viewModel = viewModel,
                )
            }
        }
    }

    @Test
    fun favoritesAreShown() {
        setContent(FakeRadioStationRepository(favorites = favorites))

        favorites.forEach { composeRule.onNodeWithText(it.name).assertIsDisplayed() }
        assertEquals(0, backCount)
    }

    @Test
    fun stationClickReportsFavoritesAndIndex() {
        val repository = FakeRadioStationRepository(favorites = favorites)
        setContent(repository)

        composeRule.onNodeWithText(favorites[1].name).performClick()

        assertEquals(repository.favorites.value to 1, clickedStation)
    }

    @Test
    fun backButtonNavigatesBack() {
        setContent(FakeRadioStationRepository(favorites = favorites))

        composeRule.onNodeWithContentDescription(targetContext.getString(R.string.back)).performClick()

        assertEquals(1, backCount)
    }

    @Test
    fun removingStationKeepsTheScreenOpen() {
        val repository = FakeRadioStationRepository(favorites = favorites)
        setContent(repository)

        composeRule.onAllNodesWithContentDescription(targetContext.getString(R.string.remove_from_favorites))[0]
            .performClick()
        composeRule.waitForIdle()

        assertEquals(favorites.drop(1).map { it.id }, repository.favorites.value.map { it.id })
        assertEquals(0, backCount)
    }

    @Test
    fun removingTheLastStationNavigatesBackOnce() {
        val repository = FakeRadioStationRepository(favorites = favorites.take(1))
        setContent(repository)

        composeRule.onNodeWithContentDescription(targetContext.getString(R.string.remove_from_favorites)).performClick()
        composeRule.waitForIdle()

        assertTrue(repository.favorites.value.isEmpty())
        assertEquals(1, backCount)
    }

    @Test
    fun screenDoesNotCloseBeforeFavoritesAreLoaded() {
        setContent(FakeRadioStationRepository(favorites = favorites))
        composeRule.waitForIdle()

        assertFalse(backCount > 0)
    }
}
