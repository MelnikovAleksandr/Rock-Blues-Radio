package ru.asmelnikov.rockbluesradio

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import ru.asmelnikov.rockbluesradio.data.di.dataModule
import ru.asmelnikov.rockbluesradio.domain.di.domainModule

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(dataModule, domainModule)
        }
    }
}