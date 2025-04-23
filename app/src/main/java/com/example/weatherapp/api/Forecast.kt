package com.example.weatherapp.api

data class Forecast(
    val forecastday: List<ForecastDay>
)

data class ForecastDay(
    val date: String,
    val day: Day
)

data class Day(
    val avgtemp_c: String,
    val avgtemp_f: String,
    val condition: Condition
)