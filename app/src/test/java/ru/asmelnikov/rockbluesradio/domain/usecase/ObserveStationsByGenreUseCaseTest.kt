package ru.asmelnikov.rockbluesradio.domain.usecase

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.asmelnikov.rockbluesradio.domain.model.Genre
import ru.asmelnikov.rockbluesradio.testing.FakeRadioStationRepository
import ru.asmelnikov.rockbluesradio.testing.testStation

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveStationsByGenreUseCaseTest {

    private val rock = listOf(testStation("r1"), testStation("r2"), testStation("r3"))
    private val blues = listOf(testStation("b1"))

    @Test
    fun `returns stations of the requested genre`() = runTest {
        val repository = FakeRadioStationRepository(mapOf(Genre.Rock to rock, Genre.Blues to blues))

        val stations = ObserveStationsByGenreUseCase(repository)(Genre.Blues).first()

        assertEquals(listOf("b1"), stations.map { it.id })
        assertEquals(listOf(Genre.Blues), repository.requestedGenres)
    }

    @Test
    fun `marks only favorite stations as favorite`() = runTest {
        val repository = FakeRadioStationRepository(
            stationsByGenre = mapOf(Genre.Rock to rock),
            favorites = listOf(testStation("r2")),
        )

        val stations = ObserveStationsByGenreUseCase(repository)(Genre.Rock).first()

        assertEquals(listOf(false, true, false), stations.map { it.isFavorite })
    }

    @Test
    fun `emits updated flags when favorites change`() = runTest {
        val repository = FakeRadioStationRepository(mapOf(Genre.Rock to rock))
        val emissions = mutableListOf<List<Boolean>>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            ObserveStationsByGenreUseCase(repository)(Genre.Rock)
                .take(2)
                .toList()
                .forEach { stations -> emissions += stations.map { it.isFavorite } }
        }

        repository.toggleFavorite(rock[0])
        job.join()

        assertEquals(listOf(listOf(false, false, false), listOf(true, false, false)), emissions)
    }

    @Test
    fun `returns empty list for genre without stations`() = runTest {
        val stations = ObserveStationsByGenreUseCase(FakeRadioStationRepository())(Genre.Rock).first()

        assertTrue(stations.isEmpty())
    }
}
