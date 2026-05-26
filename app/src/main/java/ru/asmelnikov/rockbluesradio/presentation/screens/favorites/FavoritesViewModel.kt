package ru.asmelnikov.rockbluesradio.presentation.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation
import ru.asmelnikov.rockbluesradio.domain.usecase.AddToOrRemoveFromFavoritesUseCase
import ru.asmelnikov.rockbluesradio.domain.usecase.GetFavoriteRadioStationsUseCase

class FavoritesViewModel(
    private val getFavoriteRadioStationsUseCase: GetFavoriteRadioStationsUseCase,
    private val addToOrRemoveFromFavoritesUseCase: AddToOrRemoveFromFavoritesUseCase
): ViewModel() {
    private val _favorites = MutableStateFlow<List<RadioStation>>(emptyList())
    val favorites = _favorites.asStateFlow()

    private val _events = MutableSharedFlow<FavoritesEvent>()
    val events = _events.asSharedFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getFavoriteRadioStationsUseCase.execute()
                .distinctUntilChanged()
                .collect {
                    _favorites.emit(it)
                    _events.emit(FavoritesEvent.OnItemsUpdate)
                }
        }
    }

    fun removeItem(item: RadioStation) {
        viewModelScope.launch(Dispatchers.IO) {
            addToOrRemoveFromFavoritesUseCase.execute(item)
        }
    }
}

sealed class FavoritesEvent {
    data object OnItemsUpdate: FavoritesEvent()
}