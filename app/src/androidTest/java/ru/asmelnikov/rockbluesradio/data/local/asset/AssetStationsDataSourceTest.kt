package ru.asmelnikov.rockbluesradio.data.local.asset

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import ru.asmelnikov.rockbluesradio.domain.model.Genre
import ru.asmelnikov.rockbluesradio.testing.targetContext

@RunWith(AndroidJUnit4::class)
class AssetStationsDataSourceTest {

    private val dataSource = AssetStationsDataSource(targetContext)

    @Test
    fun everyGenreHasPlayableStations() = runTest {
        Genre.entries.forEach { genre ->
            val stations = dataSource.getStations(genre)

            assertTrue("$genre is empty", stations.isNotEmpty())
            assertTrue(stations.all { it.stationUuid.isNotBlank() })
            assertTrue(stations.all { !it.urlResolved.isNullOrBlank() })
        }
    }

    @Test
    fun stationIdsAreUniqueAcrossGenres() = runTest {
        val ids = Genre.entries.flatMap { genre -> dataSource.getStations(genre).map { it.stationUuid } }

        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun stationsAreReadOnce() = runTest {
        assertSame(dataSource.getStations(Genre.Rock), dataSource.getStations(Genre.Rock))
    }
}
