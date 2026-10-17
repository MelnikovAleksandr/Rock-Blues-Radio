package ru.asmelnikov.rockbluesradio.playback.di

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import ru.asmelnikov.rockbluesradio.playback.PlaybackCatalog

val playbackModule = module {
    factoryOf(::PlaybackCatalog)
}
