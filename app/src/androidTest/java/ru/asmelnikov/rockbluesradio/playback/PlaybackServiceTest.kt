package ru.asmelnikov.rockbluesradio.playback

import androidx.media3.common.MediaItem
import androidx.media3.session.MediaBrowser
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import ru.asmelnikov.rockbluesradio.data.local.asset.AssetStationsDataSource
import ru.asmelnikov.rockbluesradio.domain.model.Genre
import ru.asmelnikov.rockbluesradio.testing.await
import ru.asmelnikov.rockbluesradio.testing.awaitPlaybackServiceStopped
import ru.asmelnikov.rockbluesradio.testing.connectMediaBrowser
import ru.asmelnikov.rockbluesradio.testing.onMainThread
import ru.asmelnikov.rockbluesradio.testing.targetContext
import ru.asmelnikov.rockbluesradio.testing.waitUntil

@RunWith(AndroidJUnit4::class)
class PlaybackServiceTest {

    private var browser: MediaBrowser? = null

    @Before
    fun setUp() {
        awaitPlaybackServiceStopped()
        browser = connectMediaBrowser()
    }

    @After
    fun tearDown() {
        browser?.let { onMainThread { it.release() } }
        awaitPlaybackServiceStopped()
    }

    private fun connectedBrowser(): MediaBrowser = checkNotNull(browser)

    private fun children(parentId: String): List<MediaItem> =
        onMainThread { connectedBrowser().getChildren(parentId, 0, Int.MAX_VALUE, null) }.await().value.orEmpty()

    private fun queueMediaIds(): List<String> = onMainThread {
        val browser = connectedBrowser()
        List(browser.mediaItemCount) { browser.getMediaItemAt(it).mediaId }
    }

    @Test
    fun libraryRootIsBrowsable() {
        val root = onMainThread { connectedBrowser().getLibraryRoot(null) }.await().value

        assertEquals(PlaybackContract.ROOT_ID, root?.mediaId)
        assertTrue(root?.mediaMetadata?.isBrowsable == true)
    }

    @Test
    fun rootListsGenreFolders() {
        val folderIds = children(PlaybackContract.ROOT_ID).map { it.mediaId }

        assertTrue(folderIds.containsAll(listOf(StationFolder.Rock.mediaId, StationFolder.Blues.mediaId)))
    }

    @Test
    fun genreFoldersAreFilledRightAfterConnecting() = runTest {
        val dataSource = AssetStationsDataSource(targetContext)

        Genre.entries.forEach { genre ->
            val folder = StationFolder.of(genre)
            val expectedIds = dataSource.getStations(genre).map {
                PlaybackContract.stationMediaId(folder, it.stationUuid)
            }

            assertEquals(expectedIds, children(folder.mediaId).map { it.mediaId })
        }
    }

    @Test
    fun stationItemIsResolvedById() {
        val firstRock = children(StationFolder.Rock.mediaId).first()

        val item = onMainThread { connectedBrowser().getItem(firstRock.mediaId) }.await().value

        assertEquals(firstRock.mediaId, item?.mediaId)
        assertEquals(firstRock.mediaMetadata.displayTitle, item?.mediaMetadata?.displayTitle)
        assertTrue(item?.mediaMetadata?.isPlayable == true)
    }

    @Test
    fun selectingStationInBrowserQueuesItsWholeFolder() {
        val blues = children(StationFolder.Blues.mediaId)
        val selected = blues[1]

        onMainThread { connectedBrowser().setMediaItem(MediaItem.Builder().setMediaId(selected.mediaId).build()) }
        waitUntil { queueMediaIds().size == blues.size }

        assertEquals(blues.drop(1).map { it.mediaId } + blues.first().mediaId, queueMediaIds())
        onMainThread { connectedBrowser().clearMediaItems() }
    }

    @Test
    fun browserReconnectsAfterServiceWasDestroyed() {
        onMainThread { connectedBrowser().release() }
        browser = null
        awaitPlaybackServiceStopped()

        browser = connectMediaBrowser()

        assertTrue(onMainThread { connectedBrowser().isConnected })
    }
}
