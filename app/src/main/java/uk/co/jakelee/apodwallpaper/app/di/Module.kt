package uk.co.jakelee.apodwallpaper.app.di

import okhttp3.OkHttpClient
import uk.co.jakelee.apodwallpaper.model.ApodApi
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import uk.co.jakelee.apodwallpaper.ui.browse.BrowseViewModel

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

val viewModelScope = module {
    viewModel { BrowseViewModel(get()) }
}

val apiModule = module {
    fun provideUserApi(retrofit: Retrofit): ApodApi {
        return retrofit.create(ApodApi::class.java)
    }

    single { provideUserApi(get()) }
}