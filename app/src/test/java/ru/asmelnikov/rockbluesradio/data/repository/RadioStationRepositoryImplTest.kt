package ru.asmelnikov.rockbluesradio.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.asmelnikov.rockbluesradio.data.local.asset.RadioStationDto
import ru.asmelnikov.rockbluesradio.data.local.asset.StationsDataSource
import ru.asmelnikov.rockbluesradio.data.local.database.FavoriteStationDao
import ru.asmelnikov.rockbluesradio.data.local.database.FavoriteStationEntity
import ru.asmelnikov.rockbluesradio.domain.model.Genre
import ru.asmelnikov.rockbluesradio.testing.testStation

class RadioStationRepositoryImplTest {

    private val dataSource = FakeStationsDataSource(
        mapOf(
            Genre.Rock to listOf(RadioStationDto(stationUuid = "r1", name = "Rock One", tags = "rock")),
            Genre.Blues to listOf(RadioStationDto(stationUuid = "b1", name = "Blues One")),
        ),
    )
    private val dao = FakeFavoriteStationDao()
    private val repository = RadioStationRepositoryImpl(dataSource, dao)

    @Test
    fun `stations of a genre are read from the data source and mapped`() = runTest {
        val stations = repository.getStationsByGenre(Genre.Rock)

        assertEquals(listOf("r1"), stations.map { it.id })
        assertEquals(listOf("Rock"), stations.single().genres)
        assertEquals(listOf(Genre.Rock), dataSource.requestedGenres)
    }

    @Test
    fun `toggle adds a station to favorites`() = runTest {
        repository.toggleFavorite(testStation("r1"))

        val favorites = repository.observeFavoriteStations().first()

        assertEquals(listOf("r1"), favorites.map { it.id })
        assertTrue(favorites.single().isFavorite)
    }

    @Test
    fun `second toggle removes the station from favorites`() = runTest {
        val station = testStation("r1")

        repository.toggleFavorite(station)
        repository.toggleFavorite(station)

        assertTrue(repository.observeFavoriteStations().first().isEmpty())
    }

    private class FakeStationsDataSource(private val stations: Map<Genre, List<RadioStationDto>>) : StationsDataSource {
        val requestedGenres = mutableListOf<Genre>()

        override suspend fun getStations(genre: Genre): List<RadioStationDto> {
            requestedGenres += genre
            return stations[genre].orEmpty()
        }
    }

    private class FakeFavoriteStationDao : FavoriteStationDao() {
        private val entities = MutableStateFlow<List<FavoriteStationEntity>>(emptyList())

        override fun observeAll(): Flow<List<FavoriteStationEntity>> = entities

        override suspend fun exists(stationId: String): Boolean = entities.value.any { it.stationUuid == stationId }

        override suspend fun insert(station: FavoriteStationEntity) {
            if (!exists(station.stationUuid)) entities.value = listOf(station) + entities.value
        }

        override suspend fun delete(stationId: String) {
            entities.value = entities.value.filterNot { it.stationUuid == stationId }
        }
    }
}
