package ru.asmelnikov.rockbluesradio

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import ru.asmelnikov.rockbluesradio.data.di.dataModule
import ru.asmelnikov.rockbluesradio.domain.di.domainModule
import ru.asmelnikov.rockbluesradio.playback.di.playbackModule
import ru.asmelnikov.rockbluesradio.presentation.di.presentationModule

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(dataModule, domainModule, playbackModule, presentationModule)
        }
    }
}
