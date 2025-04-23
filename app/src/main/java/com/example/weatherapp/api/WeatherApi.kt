package com.example.weatherapp.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("/v1/forecast.json")
    suspend fun getWeather(
        @Query("key") apikey: String,
        @Query("q") city: String,
        @Query("region") region: String,
        @Query("days") days: Int = 7
    ): Response<WeatherModel>

    @GET("v1/search.json")
    suspend fun searchCities(
        @Query("key") apiKey: String,
        @Query("q") query: String
    ): List<City>
}