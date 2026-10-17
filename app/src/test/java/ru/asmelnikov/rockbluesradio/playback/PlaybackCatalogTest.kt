package ru.asmelnikov.rockbluesradio.playback

import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.asmelnikov.rockbluesradio.domain.model.Genre
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation
import ru.asmelnikov.rockbluesradio.domain.usecase.ObserveFavoriteStationsUseCase
import ru.asmelnikov.rockbluesradio.domain.usecase.ObserveStationsByGenreUseCase
import ru.asmelnikov.rockbluesradio.testing.FakeRadioStationRepository
import ru.asmelnikov.rockbluesradio.testing.testStation
import ru.asmelnikov.rockbluesradio.testing.testStations

class PlaybackCatalogTest {

    private val rock = testStations("rock", 4)
    private val blues = testStations("blues", 2)

    private fun catalog(repository: FakeRadioStationRepository) = PlaybackCatalog(
        observeStationsByGenre = ObserveStationsByGenreUseCase(repository),
        observeFavoriteStations = ObserveFavoriteStationsUseCase(repository),
    )

    private fun repository(favorites: List<RadioStation> = emptyList()) =
        FakeRadioStationRepository(mapOf(Genre.Rock to rock, Genre.Blues to blues), favorites)

    @Test
    fun `awaitLoaded suspends until the catalog is loaded`() = runTest {
        val catalog = catalog(repository())
        val awaiting = async { catalog.awaitLoaded() }
        testScheduler.runCurrent()

        assertFalse(awaiting.isCompleted)
        catalog.load()
        awaiting.await()
        assertTrue(awaiting.isCompleted)
    }

    @Test
    fun `load fills every folder`() = runTest {
        val catalog = catalog(repository(favorites = listOf(blues[1])))

        catalog.load()

        assertEquals(rock.map { it.id }, catalog.stations(StationFolder.Rock).map { it.id })
        assertEquals(blues.map { it.id }, catalog.stations(StationFolder.Blues).map { it.id })
        assertEquals(listOf(blues[1].id), catalog.favoriteStations.map { it.id })
    }

    @Test
    fun `load completes with empty folders when stations cannot be read`() = runTest {
        val repository = repository().apply { stationsError = IllegalStateException("broken asset") }
        val catalog = catalog(repository)

        catalog.load()
        catalog.awaitLoaded()

        assertTrue(catalog.stations(StationFolder.Rock).isEmpty())
        assertTrue(catalog.stations(StationFolder.Blues).isEmpty())
    }

    @Test
    fun `root contains favorites folder only when favorites exist`() = runTest {
        val catalog = catalog(repository())
        catalog.load()

        assertEquals(listOf(StationFolder.Rock, StationFolder.Blues), catalog.rootFolders())

        catalog.setFavorites(listOf(rock[0]))
        assertEquals(StationFolder.entries, catalog.rootFolders())
    }

    @Test
    fun `child count is reported for root and folders`() = runTest {
        val catalog = catalog(repository(favorites = listOf(rock[0])))
        catalog.load()

        assertEquals(3, catalog.childCount(PlaybackContract.ROOT_ID))
        assertEquals(4, catalog.childCount(StationFolder.Rock.mediaId))
        assertEquals(2, catalog.childCount(StationFolder.Blues.mediaId))
        assertEquals(1, catalog.childCount(StationFolder.Favorites.mediaId))
        assertEquals(0, catalog.childCount("unknown"))
    }

    @Test
    fun `locate uses the folder from the media id first`() = runTest {
        val catalog = catalog(repository(favorites = listOf(rock[2])))
        catalog.load()

        val located = catalog.locate(PlaybackContract.stationMediaId(StationFolder.Favorites, rock[2].id))

        assertEquals(StationFolder.Favorites, located?.folder)
        assertTrue(located!!.station.isFavorite)
    }

    @Test
    fun `locate falls back to other folders`() = runTest {
        val catalog = catalog(repository())
        catalog.load()

        val located = catalog.locate(blues[0].id)

        assertEquals(FolderStation(StationFolder.Blues, blues[0]), located)
    }

    @Test
    fun `locate returns null for an unknown station`() = runTest {
        val catalog = catalog(repository())
        catalog.load()

        assertNull(catalog.locate("rock_missing"))
    }

    @Test
    fun `queue starts at the selected station and wraps around`() = runTest {
        val catalog = catalog(repository())
        catalog.load()

        val queue = catalog.queueStartingAt(PlaybackContract.stationMediaId(StationFolder.Rock, rock[2].id))

        assertEquals(StationFolder.Rock, queue?.folder)
        assertEquals(listOf(rock[2], rock[3], rock[0], rock[1]).map { it.id }, queue!!.stations.map { it.id })
    }

    @Test
    fun `queue is null without folder or for unknown station`() = runTest {
        val catalog = catalog(repository())
        catalog.load()

        assertNull(catalog.queueStartingAt(rock[0].id))
        assertNull(catalog.queueStartingAt(PlaybackContract.stationMediaId(StationFolder.Rock, "missing")))
    }

    @Test
    fun `favorites can be replaced`() = runTest {
        val catalog = catalog(repository())
        catalog.load()

        catalog.setFavorites(listOf(testStation("x", isFavorite = true)))

        assertEquals(listOf("x"), catalog.favoriteStations.map { it.id })
    }
}
