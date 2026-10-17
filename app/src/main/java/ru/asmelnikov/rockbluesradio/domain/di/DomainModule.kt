package ru.asmelnikov.rockbluesradio.domain.di

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import ru.asmelnikov.rockbluesradio.domain.usecase.ObserveFavoriteStationsUseCase
import ru.asmelnikov.rockbluesradio.domain.usecase.ObserveFavoritesCountUseCase
import ru.asmelnikov.rockbluesradio.domain.usecase.ObserveStationsByGenreUseCase
import ru.asmelnikov.rockbluesradio.domain.usecase.ToggleFavoriteUseCase

val domainModule = module {
    factoryOf(::ObserveStationsByGenreUseCase)
    factoryOf(::ObserveFavoriteStationsUseCase)
    factoryOf(::ObserveFavoritesCountUseCase)
    factoryOf(::ToggleFavoriteUseCase)
}
