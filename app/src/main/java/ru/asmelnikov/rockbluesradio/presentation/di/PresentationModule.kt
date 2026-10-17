package ru.asmelnikov.rockbluesradio.presentation.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import ru.asmelnikov.rockbluesradio.presentation.app.MainViewModel
import ru.asmelnikov.rockbluesradio.presentation.screens.favorites.FavoritesViewModel
import ru.asmelnikov.rockbluesradio.presentation.screens.home.HomeViewModel

val presentationModule = module {
    viewModelOf(::MainViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::FavoritesViewModel)
}
