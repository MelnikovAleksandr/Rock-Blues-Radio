package ru.asmelnikov.rockbluesradio.domain.usecase

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.asmelnikov.rockbluesradio.testing.FakeRadioStationRepository
import ru.asmelnikov.rockbluesradio.testing.testStation

class FavoriteUseCasesTest {

    private val repository = FakeRadioStationRepository()

    @Test
    fun `toggle adds a station that is not favorite`() = runTest {
        val station = testStation("s1")

        ToggleFavoriteUseCase(repository)(station)

        assertEquals(listOf("s1"), repository.favorites.value.map { it.id })
    }

    @Test
    fun `toggle removes a station that is already favorite`() = runTest {
        val station = testStation("s1")
        val toggle = ToggleFavoriteUseCase(repository)

        toggle(station)
        toggle(station)

        assertTrue(repository.favorites.value.isEmpty())
        assertEquals(listOf(station, station), repository.toggledStations)
    }

    @Test
    fun `observe favorites returns repository favorites`() = runTest {
        repository.toggleFavorite(testStation("s1"))
        repository.toggleFavorite(testStation("s2"))

        val favorites = ObserveFavoriteStationsUseCase(repository)().first()

        assertEquals(listOf("s2", "s1"), favorites.map { it.id })
        assertTrue(favorites.all { it.isFavorite })
    }

    @Test
    fun `favorites count follows favorites`() = runTest {
        val count = ObserveFavoritesCountUseCase(repository)

        assertEquals(0, count().first())
        repository.toggleFavorite(testStation("s1"))
        repository.toggleFavorite(testStation("s2"))
        assertEquals(2, count().first())
    }
}
