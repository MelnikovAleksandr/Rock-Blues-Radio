package ru.asmelnikov.rockbluesradio.presentation.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.asmelnikov.rockbluesradio.domain.model.Genre
import ru.asmelnikov.rockbluesradio.domain.model.RadioStation
import ru.asmelnikov.rockbluesradio.domain.usecase.AddToOrRemoveFromFavoritesUseCase
import ru.asmelnikov.rockbluesradio.domain.usecase.GetFavoritesRadioCountUseCase
import ru.asmelnikov.rockbluesradio.domain.usecase.GetRadioStationsUseCase

class HomeViewModel(
    private val getRadioStationsUseCase: GetRadioStationsUseCase,
    private val addToOrRemoveFromFavoritesUseCase: AddToOrRemoveFromFavoritesUseCase,
    private val getFavoritesRadioCountUseCase: GetFavoritesRadioCountUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ScreenState(isLoading = true))
    val state = _state.asStateFlow()

    init {
        loadItems()
        getFavoritesCount()
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

    fun onGenreClick() {
        viewModelScope.launch {
            _state.update { state ->
                val genres = Genre.entries
                val currentIndex = genres.indexOf(state.genre)
                val nextGenre = genres[(currentIndex + 1) % genres.size]
                state.copy(genre = nextGenre)
            }
        }
    }

    private fun getFavoritesCount() {
        viewModelScope.launch(Dispatchers.IO) {
            getFavoritesRadioCountUseCase.execute().collectLatest { count ->
                _state.update { state ->
                    state.copy(showFavoritesButton = count > 0)
                }
            }
        }
    }

    private fun loadItems() {
        viewModelScope.launch(Dispatchers.IO) {

            _state.map { it.genre }.distinctUntilChanged().collectLatest { genre ->
                _state.update { it.copy(isLoading = true, error = null) }

                getRadioStationsUseCase.execute(genre)
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
    }

}

data class ScreenState(
    val isLoading: Boolean = false,
    val items: List<RadioStation> = emptyList(),
    val error: String? = null,
    val showFavoritesButton: Boolean = false,
    val genre: Genre = Genre.Rock
)