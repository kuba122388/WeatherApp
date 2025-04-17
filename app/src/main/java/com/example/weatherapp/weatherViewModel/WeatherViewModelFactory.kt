package com.example.weatherapp.weatherViewModel

import SharedPreferencesHelper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class WeatherViewModelFactory(private val sharedPreferencesHelper: SharedPreferencesHelper) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            return WeatherViewModel(sharedPreferencesHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
