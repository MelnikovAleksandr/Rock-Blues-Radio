package ru.asmelnikov.rockbluesradio.presentation.player

import androidx.compose.runtime.Immutable

@Immutable
data class NowPlaying(val stationId: String? = null, val isMiniPlayerVisible: Boolean = false)
