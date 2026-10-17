package ru.asmelnikov.rockbluesradio.playback

import ru.asmelnikov.rockbluesradio.domain.model.Genre

enum class StationFolder(val mediaId: String) {
    Rock("rock"),
    Blues("blues"),
    Favorites("favorites"),
    ;

    companion object {
        fun of(genre: Genre): StationFolder = when (genre) {
            Genre.Rock -> Rock
            Genre.Blues -> Blues
        }

        fun fromFolderId(folderId: String): StationFolder? = entries.firstOrNull { it.mediaId == folderId }
    }
}
