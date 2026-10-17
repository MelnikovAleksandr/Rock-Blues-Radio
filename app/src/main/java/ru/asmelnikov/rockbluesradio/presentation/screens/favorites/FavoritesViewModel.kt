package ru.asmelnikov.rockbluesradio.presentation.screens.favorites

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation
import ru.asmelnikov.rockbluesradio.domain.usecase.ObserveFavoriteStationsUseCase
import ru.asmelnikov.rockbluesradio.domain.usecase.ToggleFavoriteUseCase

class FavoritesViewModel(
    observeFavoriteStations: ObserveFavoriteStationsUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
) : ViewModel() {

    val state: StateFlow<FavoritesUiState> = observeFavoriteStations()
        .map { FavoritesUiState(stations = it, isLoaded = true) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, FavoritesUiState())

    fun onFavoriteClick(station: RadioStation) {
        viewModelScope.launch { toggleFavorite(station) }
    }
}

@Immutable
data class FavoritesUiState(val stations: List<RadioStation> = emptyList(), val isLoaded: Boolean = false) {
    val isEmpty: Boolean
        get() = isLoaded && stations.isEmpty()
}
