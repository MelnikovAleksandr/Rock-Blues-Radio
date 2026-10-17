package ru.asmelnikov.rockbluesradio.playback

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.first
import ru.asmelnikov.rockbluesradio.domain.model.Genre
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation
import ru.asmelnikov.rockbluesradio.domain.usecase.ObserveFavoriteStationsUseCase
import ru.asmelnikov.rockbluesradio.domain.usecase.ObserveStationsByGenreUseCase

data class FolderStation(val folder: StationFolder, val station: RadioStation)

data class StationQueue(val folder: StationFolder, val stations: List<RadioStation>)

class PlaybackCatalog(
    private val observeStationsByGenre: ObserveStationsByGenreUseCase,
    private val observeFavoriteStations: ObserveFavoriteStationsUseCase,
) {
    private val loaded = CompletableDeferred<Unit>()
    private val stationsByFolder = mutableMapOf<StationFolder, List<RadioStation>>()

    val favoriteStations: List<RadioStation>
        get() = stations(StationFolder.Favorites)

    suspend fun load() {
        try {
            Genre.entries.forEach { genre ->
                stationsByFolder[StationFolder.of(genre)] = runCatching { observeStationsByGenre(genre).first() }
                    .getOrDefault(emptyList())
            }
            setFavorites(runCatching { observeFavoriteStations().first() }.getOrDefault(emptyList()))
        } finally {
            loaded.complete(Unit)
        }
    }

    suspend fun awaitLoaded() {
        loaded.await()
    }

    fun setFavorites(favorites: List<RadioStation>) {
        stationsByFolder[StationFolder.Favorites] = favorites
    }

    fun stations(folder: StationFolder): List<RadioStation> = stationsByFolder[folder].orEmpty()

    fun rootFolders(): List<StationFolder> = buildList {
        add(StationFolder.Rock)
        add(StationFolder.Blues)
        if (favoriteStations.isNotEmpty()) add(StationFolder.Favorites)
    }

    fun childCount(parentId: String): Int = when (parentId) {
        PlaybackContract.ROOT_ID -> rootFolders().size
        else -> StationFolder.fromFolderId(parentId)?.let { stations(it).size } ?: 0
    }

    fun locate(mediaId: String): FolderStation? {
        val stationId = PlaybackContract.stationId(mediaId)
        val searchOrder = listOfNotNull(PlaybackContract.folderOf(mediaId)) + StationFolder.entries
        return searchOrder.firstNotNullOfOrNull { folder ->
            stations(folder).find { it.id == stationId }?.let { FolderStation(folder, it) }
        }
    }

    fun queueStartingAt(mediaId: String): StationQueue? {
        val folder = PlaybackContract.folderOf(mediaId) ?: return null
        val stations = stations(folder)
        val stationId = PlaybackContract.stationId(mediaId)
        val selectedIndex = stations.indexOfFirst { it.id == stationId }
        if (selectedIndex == -1) return null
        return StationQueue(folder, stations.drop(selectedIndex) + stations.take(selectedIndex))
    }
}
