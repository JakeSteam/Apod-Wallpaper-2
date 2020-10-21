package uk.co.jakelee.apodwallpaper.app.di

import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import uk.co.jakelee.apodwallpaper.network.ApodApi
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import uk.co.jakelee.apodwallpaper.app.ApodDateParser
import uk.co.jakelee.apodwallpaper.app.database.ApodRepository
import uk.co.jakelee.apodwallpaper.app.database.AppDatabase
import uk.co.jakelee.apodwallpaper.ui.browse.architecture.BrowseViewModel
import uk.co.jakelee.apodwallpaper.ui.item.architecture.ItemViewModel

val netModule = module {
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .build()
    }
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.nasa.gov/planetary/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()
    }

    single { provideOkHttpClient() }
    single { provideRetrofit(get()) }
}

val databaseModule = module {
    single { AppDatabase.buildDatabase(androidApplication()).apodDao() }
    single { ApodDateParser() }
    single { ApodRepository(get(), get(), get()) }
}

val viewModelScope = module {
    viewModel { ItemViewModel(get(), get()) }
    viewModel { BrowseViewModel(get()) }
}

val apiModule = module {
    fun provideUserApi(retrofit: Retrofit): ApodApi {
        return retrofit.create(ApodApi::class.java)
    }

    single { provideUserApi(get()) }
}