package ru.asmelnikov.rockbluesradio.presentation.screens.home

import androidx.compose.runtime.Immutable
import ru.asmelnikov.rockbluesradio.domain.model.Genre
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation

@Immutable
data class HomeUiState(
    val isLoading: Boolean = true,
    val stationsByGenre: Map<Genre, List<RadioStation>> = emptyMap(),
    val error: String? = null,
    val showFavoritesButton: Boolean = false,
) {
    fun stationsOf(genre: Genre): List<RadioStation> = stationsByGenre[genre].orEmpty()
}
