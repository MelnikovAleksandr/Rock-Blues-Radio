package ru.asmelnikov.rockbluesradio.data.local.database

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import ru.asmelnikov.rockbluesradio.testing.targetContext

@RunWith(AndroidJUnit4::class)
class FavoriteStationDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: FavoriteStationDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(targetContext, AppDatabase::class.java).build()
        dao = database.favoriteStationDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun entity(id: String) = FavoriteStationEntity(
        favicon = "icon-$id",
        name = "Station $id",
        stationUuid = id,
        tags = "Rock",
        urlResolved = "https://stream.example/$id",
    )

    @Test
    fun toggleAddsMissingStation() = runTest {
        dao.toggle(entity("a"))

        assertEquals(listOf(entity("a")), dao.observeAll().first())
        assertTrue(dao.exists("a"))
    }

    @Test
    fun toggleRemovesExistingStation() = runTest {
        dao.toggle(entity("a"))
        dao.toggle(entity("a"))

        assertTrue(dao.observeAll().first().isEmpty())
        assertFalse(dao.exists("a"))
    }

    @Test
    fun lastAddedStationComesFirst() = runTest {
        dao.toggle(entity("a"))
        dao.toggle(entity("b"))
        dao.toggle(entity("c"))

        assertEquals(listOf("c", "b", "a"), dao.observeAll().first().map { it.stationUuid })
    }

    @Test
    fun duplicateInsertIsIgnored() = runTest {
        dao.insert(entity("a"))
        dao.insert(entity("a"))

        assertEquals(1, dao.observeAll().first().size)
    }

    @Test
    fun deleteRemovesOnlyMatchingStation() = runTest {
        dao.insert(entity("a"))
        dao.insert(entity("b"))

        dao.delete("a")

        assertEquals(listOf("b"), dao.observeAll().first().map { it.stationUuid })
    }

    @Test
    fun concurrentTogglesNeverCrashAndStayConsistent() = runTest {
        val toggles = 51

        (1..toggles)
            .map { async(Dispatchers.IO) { dao.toggle(entity("a")) } }
            .awaitAll()

        assertEquals(listOf("a"), dao.observeAll().first().map { it.stationUuid })
    }
}
