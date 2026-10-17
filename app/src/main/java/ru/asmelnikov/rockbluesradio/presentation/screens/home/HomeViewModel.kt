package ru.asmelnikov.rockbluesradio.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.asmelnikov.rockbluesradio.domain.model.Genre
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation
import ru.asmelnikov.rockbluesradio.domain.usecase.ObserveFavoritesCountUseCase
import ru.asmelnikov.rockbluesradio.domain.usecase.ObserveStationsByGenreUseCase
import ru.asmelnikov.rockbluesradio.domain.usecase.ToggleFavoriteUseCase

class HomeViewModel(
    private val observeStationsByGenre: ObserveStationsByGenreUseCase,
    private val observeFavoritesCount: ObserveFavoritesCountUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        Genre.entries.forEach(::observeStations)
        observeFavoritesButtonVisibility()
    }

    fun onFavoriteClick(station: RadioStation) {
        viewModelScope.launch { toggleFavorite(station) }
    }

    private fun observeStations(genre: Genre) {
        observeStationsByGenre(genre)
            .onStart { _state.update { it.copy(isLoading = true, error = null) } }
            .onEach { stations ->
                _state.update { it.copy(isLoading = false, stationsByGenre = it.stationsByGenre + (genre to stations)) }
            }
            .catch { error -> _state.update { it.copy(isLoading = false, error = error.message) } }
            .launchIn(viewModelScope)
    }

    private fun observeFavoritesButtonVisibility() {
        observeFavoritesCount()
            .onEach { count -> _state.update { it.copy(showFavoritesButton = count > 0) } }
            .launchIn(viewModelScope)
    }
}
