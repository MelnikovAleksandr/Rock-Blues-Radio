package ru.asmelnikov.rockbluesradio.playback

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import ru.asmelnikov.rockbluesradio.R
import ru.asmelnikov.rockbluesradio.domain.model.Genre
import ru.asmelnikov.rockbluesradio.domain.usecase.ObserveFavoriteStationsUseCase
import ru.asmelnikov.rockbluesradio.domain.usecase.ObserveStationsByGenreUseCase
import ru.asmelnikov.rockbluesradio.testing.FakeRadioStationRepository
import ru.asmelnikov.rockbluesradio.testing.targetContext
import ru.asmelnikov.rockbluesradio.testing.testStations

@RunWith(AndroidJUnit4::class)
class LibraryItemFactoryTest {

    private val rock = testStations("rock", 3)
    private val blues = testStations("blues", 2)
    private val repository = FakeRadioStationRepository(mapOf(Genre.Rock to rock, Genre.Blues to blues))
    private val catalog = PlaybackCatalog(
        ObserveStationsByGenreUseCase(repository),
        ObserveFavoriteStationsUseCase(repository),
    )
    private val factory = LibraryItemFactory(targetContext)

    @Before
    fun setUp() = runTest {
        catalog.load()
    }

    @Test
    fun rootIsBrowsable() {
        val root = factory.root()

        assertEquals(PlaybackContract.ROOT_ID, root.mediaId)
        assertTrue(root.mediaMetadata.isBrowsable == true)
        assertFalse(root.mediaMetadata.isPlayable == true)
    }

    @Test
    fun rootChildrenAreTitledFolders() {
        catalog.setFavorites(listOf(rock[0]))

        val folders = factory.children(catalog, PlaybackContract.ROOT_ID)

        assertEquals(StationFolder.entries.map { it.mediaId }, folders.map { it.mediaId })
        assertEquals(
            listOf(R.string.rock, R.string.blues, R.string.favorite).map(targetContext::getString),
            folders.map { it.mediaMetadata.title.toString() },
        )
        assertTrue(folders.all { it.mediaMetadata.isBrowsable == true && it.mediaMetadata.artworkUri != null })
    }

    @Test
    fun folderChildrenArePlayableStations() {
        val stations = factory.children(catalog, StationFolder.Blues.mediaId)

        assertEquals(
            blues.map {
                PlaybackContract.stationMediaId(StationFolder.Blues, it.id)
            },
            stations.map { it.mediaId },
        )
        assertTrue(stations.all { it.mediaMetadata.isPlayable == true && it.localConfiguration != null })
        assertEquals(blues.map { it.name }, stations.map { it.mediaMetadata.displayTitle.toString() })
    }

    @Test
    fun unknownParentHasNoChildren() {
        assertTrue(factory.children(catalog, "unknown").isEmpty())
    }

    @Test
    fun stationAndQueueKeepTheirFolder() {
        val item = factory.station(FolderStation(StationFolder.Rock, rock[1]))
        val queue = factory.queue(StationQueue(StationFolder.Favorites, rock))

        assertEquals("rock_${rock[1].id}", item.mediaId)
        assertEquals(rock[1].streamUrl, item.localConfiguration?.uri.toString())
        assertTrue(queue.all { PlaybackContract.folderOf(it.mediaId) == StationFolder.Favorites })
    }
}
