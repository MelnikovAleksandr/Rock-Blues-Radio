package ru.asmelnikov.rockbluesradio.presentation.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.asmelnikov.rockbluesradio.presentation.MainViewModel
import ru.asmelnikov.rockbluesradio.presentation.screens.favorites.FavoritesViewModel
import ru.asmelnikov.rockbluesradio.presentation.screens.main.HomeViewModel

val uiModule = module {
    viewModel { MainViewModel() }
    viewModel { HomeViewModel(get(), get()) }
    viewModel { FavoritesViewModel(get(), get()) }
}