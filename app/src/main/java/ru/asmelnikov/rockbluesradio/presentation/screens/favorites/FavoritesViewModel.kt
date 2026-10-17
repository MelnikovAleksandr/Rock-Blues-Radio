package ru.asmelnikov.rockbluesradio.presentation.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation
import ru.asmelnikov.rockbluesradio.domain.usecase.ObserveFavoriteStationsUseCase
import ru.asmelnikov.rockbluesradio.domain.usecase.ToggleFavoriteUseCase

class FavoritesViewModel(
    observeFavoriteStations: ObserveFavoriteStationsUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
) : ViewModel() {

    private val _favorites = MutableStateFlow<List<RadioStation>>(emptyList())
    val favorites: StateFlow<List<RadioStation>> = _favorites.asStateFlow()

    private val _events = MutableSharedFlow<FavoritesEvent>()
    val events: SharedFlow<FavoritesEvent> = _events.asSharedFlow()

    init {
        observeFavoriteStations()
            .distinctUntilChanged()
            .onEach { favorites ->
                _favorites.value = favorites
                _events.emit(FavoritesEvent.FavoritesChanged(favorites))
                if (favorites.isEmpty()) {
                    _events.emit(FavoritesEvent.NavigateBack)
                }
            }
            .launchIn(viewModelScope)
    }

    fun onFavoriteClick(station: RadioStation) {
        viewModelScope.launch { toggleFavorite(station) }
    }
}

sealed interface FavoritesEvent {
    data class FavoritesChanged(val favorites: List<RadioStation>) : FavoritesEvent
    data object NavigateBack : FavoritesEvent
}
