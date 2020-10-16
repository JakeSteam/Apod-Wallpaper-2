package uk.co.jakelee.apodwallpaper.network

import retrofit2.http.GET
import retrofit2.http.Query
import uk.co.jakelee.apodwallpaper.model.Apod

interface ApodApi {

    @GET("apod")
    suspend fun getApods(
        @Query("api_key") apiKey: String,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): List<Apod>

    @GET("apod")
    suspend fun getApod(
        @Query("api_key") apiKey: String,
        @Query("date") date: String
    ): Apod

    @GET("apod")
    suspend fun getLatestApod(
        @Query("api_key") apiKey: String
    ): Apod
}