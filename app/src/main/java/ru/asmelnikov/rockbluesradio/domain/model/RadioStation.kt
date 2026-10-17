package ru.asmelnikov.rockbluesradio.domain.model

data class RadioStation(
    val id: String,
    val name: String,
    val streamUrl: String,
    val faviconUrl: String,
    val isFavorite: Boolean,
    val genres: List<String>,
)
