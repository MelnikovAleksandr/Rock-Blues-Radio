package ru.asmelnikov.rockbluesradio.presentation.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import ru.asmelnikov.rockbluesradio.domain.connectivity.ConnectivityObserver

class MainViewModel(connectivityObserver: ConnectivityObserver) : ViewModel() {

    val isConnected: StateFlow<Boolean> = connectivityObserver.isConnected.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = true,
    )

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
