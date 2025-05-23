package com.example.weatherapp.weatherViewModel

import SharedPreferencesHelper
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableIntStateOf
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


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

    val temperatureUnit = mutableIntStateOf(0)
    val windSpeedUnit = mutableIntStateOf(0)
    val refreshTime = mutableIntStateOf(0)

    private val _lastUpdated = MutableLiveData("Unknown")
    val lastUpdated: LiveData<String> = _lastUpdated

    private var refreshJob: Job? = null
    private var lastResumeTime = 0L
    private var accumulatedActiveTime = 0L
    private var targetInterval = getRefreshIntervalMillis()


    init {
        loadFavorites()
        loadUserSettings()
    }

    private fun loadUserSettings() {
        val (tempUnit, windUnit, refresh) = sharedPreferencesHelper.loadSettings()
        temperatureUnit.intValue = tempUnit
        windSpeedUnit.intValue = windUnit
        refreshTime.intValue = refresh
    }

    fun saveUserSettings() {
        sharedPreferencesHelper.saveSettings(
            temperatureUnit.intValue,
            windSpeedUnit.intValue,
            refreshTime.intValue
        )
    }

    private fun getRefreshIntervalMillis(): Long {
        return when (refreshTime.intValue) {
            0 -> 1 * 60 * 1000L
            1 -> 5 * 60 * 1000L
            2 -> 10 * 60 * 1000L
            3 -> 15 * 60 * 1000L
            4 -> 30 * 60 * 1000L
            5 -> 60 * 60 * 1000L
            6 -> 24 * 60 * 60 * 1000L
            else -> {
                1 * 60 * 1000L
            }
        }
    }

    fun checkConnectivityAndLoadData(context: Context) {
        val lastCity = sharedPreferencesHelper.loadLastCity()
        _selectedCity.value = lastCity

        if (isInternetAvailable(context)) {
            getWeather(lastCity)
            fetchWeatherForFavoriteCities(context)
        } else {
            loadWeatherForCityFromFile(context, lastCity)
        }
    }


    private fun fetchWeatherForFavoriteCities(context: Context) {
        viewModelScope.launch {
            try {
                val favoriteCitiesList = sharedPreferencesHelper.loadFavoriteCities()
                for (city in favoriteCitiesList) {
                    val response = weatherApi.getWeather(
                        apikey = Constant.apiKey,
                        city = city.name+", "+city.region,
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

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }

    private fun getWeather(city: City) {
        _weatherResult.value = NetworkResponse.Loading
        fetchWeather(city)
    }

    private fun refreshWeatherSilently() {
        _selectedCity.value?.let { fetchWeather(it) }
        Log.e("MyDebug", "ODŚWIEŻANIE")
    }

    private fun fetchWeather(city: City) {
        viewModelScope.launch {
            try {
                val response = weatherApi.getWeather(
                    apikey = Constant.apiKey,
                    city = city.name+", "+city.region,
                )
                Log.e("myDebug", "${city.name} ${city.region}")
                Log.e("myDebug", "${response.body()?.location?.name} ${response.body()?.location?.region}")

                if (response.isSuccessful) {
                    response.body()?.let {
                        _weatherResult.value = NetworkResponse.Success(it)
                        sharedPreferencesHelper.saveLastChosenCity(city, it)
                        _lastUpdated.postValue(
                            SimpleDateFormat(
                                "yyyy-MM-dd HH:mm:ss",
                                Locale.getDefault()
                            ).format(Date())
                        )
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
        _favoriteCities.value = sharedPreferencesHelper.loadFavoriteCities()
    }

    fun toggleFavorite(context: Context, city: City) {
        val currentFavorites = sharedPreferencesHelper.loadFavoriteCities().toMutableList()
        val isFavorite = currentFavorites.any { it == city }

        if (isFavorite) {
            removeFavorite(context, city, currentFavorites)
        } else {
            addFavorite(context, city, currentFavorites)
        }

        sharedPreferencesHelper.saveFavoriteCities(currentFavorites)
        _favoriteCities.value = currentFavorites
    }

    private fun removeFavorite(context: Context, city: City, list: MutableList<City>) {
        list.removeAll { it == city }
        deleteWeatherForCityFromFile(context, city)
    }

    private fun addFavorite(context: Context, city: City, list: MutableList<City>) {
        list.add(city)
        saveWeatherForCityToFile(context, city)
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
                    _lastUpdated.postValue(
                        SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss",
                            Locale.getDefault()
                        ).format(Date())
                    )

                } else {
                    val fallbackWeather = sharedPreferencesHelper.loadLastChosenCity()
                    if (fallbackWeather != null) {
                        _weatherResult.postValue(NetworkResponse.Success(fallbackWeather))
                        _lastUpdated.postValue(
                            SimpleDateFormat(
                                "yyyy-MM-dd HH:mm:ss",
                                Locale.getDefault()
                            ).format(Date())
                        )

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

    private fun deleteWeatherForCityFromFile(context: Context, city: City) {
        val file = File(context.filesDir, "weather_data_for_favorites.json")
        if (file.exists()) {
            try {
                val json = file.readText()
                val weatherList = Gson().fromJson(json, Array<WeatherModel>::class.java).toMutableList()
                val removed = weatherList.removeAll { it.location.name == city.name && it.location.region == city.region }

                if (removed) {
                    file.writeText(Gson().toJson(weatherList))
                    Log.d("WeatherViewModel", "Usunięto dane pogodowe dla miasta: ${city.name}")
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

    private fun saveWeatherForCityToFile(context: Context, city: City) {
        viewModelScope.launch {
            try {
                val response = weatherApi.getWeather(
                    apikey = Constant.apiKey,
                    city = city.name+", "+city.region,
                )

                if (response.isSuccessful) {
                    val weather = response.body()
                    if (weather != null) {
                        val file = File(context.filesDir, "weather_data_for_favorites.json")
                        val gson = Gson()

                        val weatherList = if (file.exists()) {
                            try {
                                gson.fromJson(file.readText(), Array<WeatherModel>::class.java).toMutableList()
                            } catch (e: Exception) {
                                mutableListOf()
                            }
                        } else mutableListOf()

                        weatherList.add(weather)

                        try {
                            file.writeText(gson.toJson(weatherList))
                        } catch (e: Exception) {
                            Log.e("WeatherViewModel", "Błąd zapisu danych pogodowych do pliku: ${e.message}")
                        }
                    }
                } else {
                    Log.e("WeatherViewModel", "Nieudane pobieranie danych pogodowych dla ${city.name}")
                }

            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Błąd pobierania danych pogodowych: ${e.message}")
            }
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

    fun startAutoRefreshTimer(context: Context) {
        lastResumeTime = System.currentTimeMillis()

        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            while (true) {
                val now = System.currentTimeMillis()
                val elapsed = accumulatedActiveTime + (now - lastResumeTime)

                val remaining = targetInterval - elapsed
                if (remaining <= 0) {
                    if (_selectedCity.value != null && isInternetAvailable(context)) {
                        refreshWeatherSilently()
                    }

                    // Reset timer
                    lastResumeTime = System.currentTimeMillis()
                    accumulatedActiveTime = 0L
                } else {
                    delay(remaining)
                }
            }
        }
    }

    fun pauseAutoRefresh() {
        val now = System.currentTimeMillis()
        accumulatedActiveTime += now - lastResumeTime
        refreshJob?.cancel()
    }


}

