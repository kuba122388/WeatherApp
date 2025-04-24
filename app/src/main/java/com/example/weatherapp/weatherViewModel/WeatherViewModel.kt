package com.example.weatherapp.weatherViewModel

import SharedPreferencesHelper
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.api.City
import com.example.weatherapp.api.Constant
import com.example.weatherapp.api.NetworkResponse
import com.example.weatherapp.api.RetrofitInstance
import com.example.weatherapp.api.WeatherModel
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.io.File

class WeatherViewModel(private val sharedPreferencesHelper: SharedPreferencesHelper) : ViewModel() {

    private val weatherApi = RetrofitInstance.weatherApi

    private val _weatherResult = MutableLiveData<NetworkResponse<WeatherModel>>()
    val weatherResult: LiveData<NetworkResponse<WeatherModel>> = _weatherResult

    private val _citySuggestions = MutableLiveData<List<City>>()
    val citySuggestions: LiveData<List<City>> = _citySuggestions

    private val _favoriteCities = MutableLiveData<List<City>>()
    val favoriteCities: LiveData<List<City>> = _favoriteCities

    private val _selectedCity = MutableLiveData<City?>()
    // val selectedCity: LiveData<City?> = _selectedCity

    init {
        loadFavorites()
    }

    fun checkConnectivityAndLoadData(context: Context) {
        val lastCity = sharedPreferencesHelper.loadLastCity()
        _selectedCity.value = lastCity

        if (lastCity != null) {
            if (isInternetAvailable(context)) {
                getWeather(lastCity)
                fetchWeatherForFavoriteCities(context)
            } else {
                loadWeatherForCityFromFile(context, lastCity)
            }
        } else {
            _weatherResult.postValue(NetworkResponse.Error("Nie wybrano jeszcze żadnego miasta."))
        }
    }


    private fun fetchWeatherForFavoriteCities(context: Context) {
        viewModelScope.launch {
            try {
                val favoriteCitiesList = sharedPreferencesHelper.getFavoriteCities()
                for (city in favoriteCitiesList) {
                    val response = weatherApi.getWeather(
                        apikey = Constant.apiKey,
                        city = city.name,
                        region = city.region
                    )

                    if (response.isSuccessful && response.body() != null) {
                        val weather = response.body()!!
                        saveWeatherForFavorites(context, weather)
                    }
                }
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Error fetching weather: ${e.message}")
            }
        }
    }

    private fun saveWeatherForFavorites(context: Context, newWeather: WeatherModel) {
        val file = File(context.filesDir, "weather_data_for_favorites.json")
        val gson = Gson()

        val weatherList = if (file.exists()) {
            try {
                gson.fromJson(file.readText(), Array<WeatherModel>::class.java).toMutableList()
            } catch (e: Exception) {
                mutableListOf()
            }
        } else mutableListOf()

        weatherList.removeAll {
            it.location.name == newWeather.location.name &&
                    it.location.region == newWeather.location.region
        }

        weatherList.add(newWeather)

        try {
            file.writeText(gson.toJson(weatherList))
        } catch (e: Exception) {
            Log.e("WeatherViewModel", "Error saving favorite weather: ${e.message}")
        }
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }

    private fun getWeather(city: City) {
        _weatherResult.value = NetworkResponse.Loading
        viewModelScope.launch {
            try {
                val response = weatherApi.getWeather(
                    Constant.apiKey,
                    city.name,
                    city.region
                )
                if (response.isSuccessful) {
                    response.body()?.let {
                        _weatherResult.value = NetworkResponse.Success(it)
                        sharedPreferencesHelper.saveLastChosenCity(city, it)

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

    private fun loadFavorites() {
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

    private fun loadWeatherForCityFromFile(context: Context, city: City) {
        val file = File(context.filesDir, "weather_data_for_favorites.json")
        if (file.exists()) {
            try {
                Log.e("Debug", "Plik istnieje")

                val json = file.readText()
                val weatherList = Gson().fromJson(json, Array<WeatherModel>::class.java).toList()
                val cityWeather =
                    weatherList.find { it.location.name == city.name && it.location.region == city.region }

                if (cityWeather != null) {
                    _weatherResult.postValue(NetworkResponse.Success(cityWeather))
                } else {
                    val fallbackWeather = sharedPreferencesHelper.loadLastChosenCity()
                    if (fallbackWeather != null) {
                        _weatherResult.postValue(NetworkResponse.Success(fallbackWeather))
                    } else {
                        _weatherResult.postValue(NetworkResponse.Error("Brak danych pogodowych dla tego miasta."))
                    }
                }
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Błąd odczytu danych pogodowych z pliku: ${e.message}")
                _weatherResult.postValue(NetworkResponse.Error("Nie udało się wczytać danych pogodowych z pliku."))
            }
        } else {
            Log.e("WeatherViewModel", "Plik nie istnieje")

            _weatherResult.postValue(NetworkResponse.Error("Brak zapisanych danych pogodowych."))
        }
    }


    fun selectCity(city: City, context: Context) {
        _selectedCity.value = city

        if (isInternetAvailable(context)) {
            getWeather(city)
        } else {
            loadWeatherForCityFromFile(context, city)
            Toast.makeText(
                context,
                "You are offline.\nInformation may be outdated.",
                Toast.LENGTH_LONG
            ).show()
        }

    }

}

