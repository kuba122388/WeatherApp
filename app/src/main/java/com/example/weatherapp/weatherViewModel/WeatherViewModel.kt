package com.example.weatherapp.weatherViewModel

import SharedPreferencesHelper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.api.City
import com.example.weatherapp.api.Constant
import com.example.weatherapp.api.NetworkResponse
import com.example.weatherapp.api.RetrofitInstance
import com.example.weatherapp.api.WeatherModel
import kotlinx.coroutines.launch

class WeatherViewModel(private val sharedPreferencesHelper: SharedPreferencesHelper) : ViewModel() {

    private val weatherApi = RetrofitInstance.weatherApi

    private val _weatherResult = MutableLiveData<NetworkResponse<WeatherModel>>()
    val weatherResult: LiveData<NetworkResponse<WeatherModel>> = _weatherResult

    private val _citySuggestions = MutableLiveData<List<City>>()
    val citySuggestions: LiveData<List<City>> = _citySuggestions

    private val _favoriteCities = MutableLiveData<List<City>>()
    val favoriteCities: LiveData<List<City>> = _favoriteCities

    init {
        loadFavorites()
    }

    fun getData(city: String) {
        _weatherResult.value = NetworkResponse.Loading
        viewModelScope.launch {
            try {
                val response = weatherApi.getWeather(Constant.apiKey, city)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _weatherResult.value = NetworkResponse.Success(it)
                    }
                } else {
                    _weatherResult.value = NetworkResponse.Error("Failed to load data")
                }
            } catch (e: Exception) {
                _weatherResult.value = NetworkResponse.Error("Failed to load data")
            }
        }
    }

    fun searchCities(query: String) {
        viewModelScope.launch {
            try {
                val result = weatherApi.searchCities(Constant.apiKey, query)
                _citySuggestions.value = result
            } catch (e: Exception) {
                Log.e("CitySearch", "Error fetching cities: ${e.message}")
            }
        }
    }

    fun loadFavorites() {
        _favoriteCities.value = sharedPreferencesHelper.getFavoriteCities()
    }

    fun toggleFavorite(city: City) {
        val currentFavorites = sharedPreferencesHelper.getFavoriteCities().toMutableList()

        val alreadyFavorite = currentFavorites.any { it == city }

        if (alreadyFavorite) {
            currentFavorites.removeAll { it == city }
        } else {
            currentFavorites.add(city)
        }

        sharedPreferencesHelper.saveFavoriteCities(currentFavorites)
        _favoriteCities.value = currentFavorites
    }

    fun isCityFavorite(city: City): Boolean {
        return _favoriteCities.value?.any { it == city } == true
    }
}
