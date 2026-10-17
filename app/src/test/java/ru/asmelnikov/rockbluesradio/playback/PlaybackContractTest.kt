package ru.asmelnikov.rockbluesradio.playback

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import ru.asmelnikov.rockbluesradio.domain.model.Genre

class PlaybackContractTest {

    @Test
    fun `station media id combines folder and station id`() {
        assertEquals("rock_abc-123", PlaybackContract.stationMediaId(StationFolder.Rock, "abc-123"))
        assertEquals("favorites_abc", PlaybackContract.stationMediaId(StationFolder.Favorites, "abc"))
    }

    @Test
    fun `station id is extracted from media id`() {
        assertEquals("abc-123", PlaybackContract.stationId("blues_abc-123"))
    }

    @Test
    fun `media id without separator is treated as station id`() {
        assertEquals("abc", PlaybackContract.stationId("abc"))
    }

    @Test
    fun `folder is resolved for every station folder`() {
        StationFolder.entries.forEach { folder ->
            assertEquals(folder, PlaybackContract.folderOf(PlaybackContract.stationMediaId(folder, "id")))
        }
    }

    @Test
    fun `folder is null for unknown or missing prefixes`() {
        assertNull(PlaybackContract.folderOf(null))
        assertNull(PlaybackContract.folderOf("abc"))
        assertNull(PlaybackContract.folderOf("_abc"))
        assertNull(PlaybackContract.folderOf("jazz_abc"))
        assertNull(PlaybackContract.folderOf(PlaybackContract.ROOT_ID))
        assertNull(PlaybackContract.folderOf("rock"))
    }

    @Test
    fun `station folder matches genre`() {
        assertEquals(StationFolder.Rock, StationFolder.of(Genre.Rock))
        assertEquals(StationFolder.Blues, StationFolder.of(Genre.Blues))
    }

    @Test
    fun `station folder is found by folder id`() {
        StationFolder.entries.forEach { folder ->
            assertEquals(folder, StationFolder.fromFolderId(folder.mediaId))
        }
        assertNull(StationFolder.fromFolderId(PlaybackContract.ROOT_ID))
    }
}
