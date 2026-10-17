package ru.asmelnikov.rockbluesradio.presentation.screens.favorites

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import ru.asmelnikov.rockbluesradio.domain.usecase.ObserveFavoriteStationsUseCase
import ru.asmelnikov.rockbluesradio.domain.usecase.ToggleFavoriteUseCase
import ru.asmelnikov.rockbluesradio.testing.FakeRadioStationRepository
import ru.asmelnikov.rockbluesradio.testing.MainDispatcherRule
import ru.asmelnikov.rockbluesradio.testing.testStation

class FavoritesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun viewModel(repository: FakeRadioStationRepository) = FavoritesViewModel(
        observeFavoriteStations = ObserveFavoriteStationsUseCase(repository),
        toggleFavorite = ToggleFavoriteUseCase(repository),
    )

    @Test
    fun `state before loading is not empty`() {
        val state = FavoritesUiState()

        assertFalse(state.isLoaded)
        assertFalse(state.isEmpty)
    }

    @Test
    fun `loaded favorites are exposed`() {
        val repository = FakeRadioStationRepository(favorites = listOf(testStation("a"), testStation("b")))

        val state = viewModel(repository).state.value

        assertTrue(state.isLoaded)
        assertFalse(state.isEmpty)
        assertEquals(listOf("a", "b"), state.stations.map { it.id })
    }

    @Test
    fun `loaded empty favorites are reported as empty`() {
        val state = viewModel(FakeRadioStationRepository()).state.value

        assertTrue(state.isLoaded)
        assertTrue(state.isEmpty)
    }

    @Test
    fun `favorite click removes the station`() {
        val station = testStation("a")
        val repository = FakeRadioStationRepository(favorites = listOf(station, testStation("b")))
        val viewModel = viewModel(repository)

        viewModel.onFavoriteClick(viewModel.state.value.stations.first())

        assertEquals(listOf("b"), viewModel.state.value.stations.map { it.id })
    }

    @Test
    fun `removing the last favorite makes the state empty`() {
        val repository = FakeRadioStationRepository(favorites = listOf(testStation("a")))
        val viewModel = viewModel(repository)

        viewModel.onFavoriteClick(viewModel.state.value.stations.single())

        assertTrue(viewModel.state.value.isEmpty)
    }
}
