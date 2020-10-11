package uk.co.jakelee.apodwallpaper.app

import android.app.Application
import uk.co.jakelee.apodwallpaper.app.di.apiModule
import uk.co.jakelee.apodwallpaper.app.di.netModule
import uk.co.jakelee.apodwallpaper.app.di.viewModelScope
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import uk.co.jakelee.apodwallpaper.app.di.databaseModule

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(netModule, databaseModule, apiModule, viewModelScope)
        }
    }
}