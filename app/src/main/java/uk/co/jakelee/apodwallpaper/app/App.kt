package uk.co.jakelee.apodwallpaper.app

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import uk.co.jakelee.apodwallpaper.app.di.*

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(netModule, storageModule, apiModule, viewModelScope, wallpaperModule)
        }
    }
}