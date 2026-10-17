package ru.asmelnikov.rockbluesradio.presentation.screens.home

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import ru.asmelnikov.rockbluesradio.domain.model.Genre
import ru.asmelnikov.rockbluesradio.domain.usecase.ObserveFavoritesCountUseCase
import ru.asmelnikov.rockbluesradio.domain.usecase.ObserveStationsByGenreUseCase
import ru.asmelnikov.rockbluesradio.domain.usecase.ToggleFavoriteUseCase
import ru.asmelnikov.rockbluesradio.testing.FakeRadioStationRepository
import ru.asmelnikov.rockbluesradio.testing.MainDispatcherRule
import ru.asmelnikov.rockbluesradio.testing.testStations

class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val rock = testStations("rock", 3)
    private val blues = testStations("blues", 2)
    private val repository = FakeRadioStationRepository(mapOf(Genre.Rock to rock, Genre.Blues to blues))

    private fun viewModel() = HomeViewModel(
        observeStationsByGenre = ObserveStationsByGenreUseCase(repository),
        observeFavoritesCount = ObserveFavoritesCountUseCase(repository),
        toggleFavorite = ToggleFavoriteUseCase(repository),
    )

    @Test
    fun `stations of every genre are loaded`() {
        val state = viewModel().state.value

        assertEquals(rock.map { it.id }, state.stationsOf(Genre.Rock).map { it.id })
        assertEquals(blues.map { it.id }, state.stationsOf(Genre.Blues).map { it.id })
    }

    @Test
    fun `favorites button is hidden without favorites`() {
        assertFalse(viewModel().state.value.showFavoritesButton)
    }

    @Test
    fun `favorite click marks the station and shows favorites button`() {
        val viewModel = viewModel()

        viewModel.onFavoriteClick(rock[1])

        val state = viewModel.state.value
        assertEquals(listOf(false, true, false), state.stationsOf(Genre.Rock).map { it.isFavorite })
        assertTrue(state.showFavoritesButton)
        assertEquals(listOf(rock[1]), repository.toggledStations)
    }

    @Test
    fun `second favorite click removes the station and hides favorites button`() {
        val viewModel = viewModel()

        viewModel.onFavoriteClick(blues[0])
        viewModel.onFavoriteClick(blues[0])

        val state = viewModel.state.value
        assertTrue(state.stationsOf(Genre.Blues).none { it.isFavorite })
        assertFalse(state.showFavoritesButton)
    }

    @Test
    fun `favorites changed elsewhere are reflected in the lists`() {
        val viewModel = viewModel()

        repository.favorites.value = listOf(rock[0].copy(isFavorite = true))

        assertTrue(viewModel.state.value.stationsOf(Genre.Rock).first().isFavorite)
        assertTrue(viewModel.state.value.showFavoritesButton)
    }

    @Test
    fun `failed station loading results in empty lists`() {
        repository.stationsError = IllegalStateException("broken asset")

        val state = viewModel().state.value

        assertTrue(state.stationsOf(Genre.Rock).isEmpty())
        assertTrue(state.stationsOf(Genre.Blues).isEmpty())
    }
}
