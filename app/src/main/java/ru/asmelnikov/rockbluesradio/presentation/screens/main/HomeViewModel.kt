package ru.asmelnikov.rockbluesradio.presentation.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.asmelnikov.rockbluesradio.domain.model.Genre
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation
import ru.asmelnikov.rockbluesradio.domain.usecase.AddToOrRemoveFromFavoritesUseCase
import ru.asmelnikov.rockbluesradio.domain.usecase.GetRadioStationsUseCase

class HomeViewModel(
    private val getRadioStationsUseCase: GetRadioStationsUseCase,
    private val addToOrRemoveFromFavoritesUseCase: AddToOrRemoveFromFavoritesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ScreenState(isLoading = true))
    val state = _state.asStateFlow()

    init {
        loadItems()
    }

    fun loadItems() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(isLoading = true) }

            getRadioStationsUseCase.execute(Genre.Rock)
                .catch { exception ->
                    _state.update { state ->
                        state.copy(isLoading = false, error = exception.message)
                    }
                }
                .collectLatest { stations ->
                    _state.update { state ->
                        state.copy(isLoading = false, items = stations)
                    }
                }
        }
    }

    fun addOrRemoteFavorites(item: RadioStation) {
        viewModelScope.launch(Dispatchers.IO) {
            addToOrRemoveFromFavoritesUseCase.execute(item)
            _state.update { state ->
                state.copy(
                    items = state.items.map { radioStation ->
                        if (radioStation.id == item.id) {
                            radioStation.copy(isFavorite = !radioStation.isFavorite)
                        } else {
                            radioStation
                        }
                    }
                )
            }
        }
    }

}

data class ScreenState(
    val isLoading: Boolean = false,
    val items: List<RadioStation> = emptyList(),
    val error: String? = null
)