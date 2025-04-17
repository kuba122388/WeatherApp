package com.example.weatherapp.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("/v1/current.json")
    suspend fun getWeather(
        @Query("key") apikey: String,
        @Query("q") city: String
    ): Response<WeatherModel>

    @GET("v1/search.json")
    suspend fun searchCities(
        @Query("key") apiKey: String,
        @Query("q") query: String
    ): List<City>
}