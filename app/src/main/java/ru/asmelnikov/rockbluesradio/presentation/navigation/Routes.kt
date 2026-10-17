package ru.asmelnikov.rockbluesradio.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Routes : NavKey {

    @Serializable
    data object Home : Routes

    @Serializable
    data object Favorites : Routes
}
