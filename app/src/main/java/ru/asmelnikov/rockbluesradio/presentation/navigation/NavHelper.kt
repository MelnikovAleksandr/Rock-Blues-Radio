package ru.asmelnikov.rockbluesradio.presentation.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

fun NavBackStack<NavKey>.popUp() {
    this.removeLastOrNull()
}

fun NavBackStack<NavKey>.navigate(route: Routes) {
    this.add(route)
}