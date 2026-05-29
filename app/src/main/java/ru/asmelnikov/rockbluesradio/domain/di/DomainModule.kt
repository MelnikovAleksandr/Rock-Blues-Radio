package ru.asmelnikov.rockbluesradio.domain.di

import org.koin.dsl.module
import ru.asmelnikov.rockbluesradio.domain.usecase.AddToOrRemoveFromFavoritesUseCase
import ru.asmelnikov.rockbluesradio.domain.usecase.GetFavoriteRadioStationsUseCase
import ru.asmelnikov.rockbluesradio.domain.usecase.GetFavoritesRadioCountUseCase
import ru.asmelnikov.rockbluesradio.domain.usecase.GetRadioStationsUseCase

val domainModule = module {
    factory { GetRadioStationsUseCase(get()) }
    factory { GetFavoriteRadioStationsUseCase(get()) }
    factory { AddToOrRemoveFromFavoritesUseCase(get()) }
    factory { GetFavoritesRadioCountUseCase(get()) }
}