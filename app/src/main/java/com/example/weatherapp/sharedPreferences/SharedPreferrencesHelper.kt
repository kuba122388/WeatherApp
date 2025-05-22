import android.content.Context
import com.example.weatherapp.api.City
import com.example.weatherapp.api.WeatherModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferencesHelper(context: Context) {

    private val sharedPreferences = context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveSettings(tempUnit: Int, windUnit: Int, refresh: Int) {
        sharedPreferences.edit()
            .putInt("temperature_unit", tempUnit)
            .putInt("wind_speed_unit", windUnit)
            .putInt("refresh_time", refresh)
            .apply()
    }

    fun loadSettings(): Triple<Int, Int, Int> {
        val tempUnit = sharedPreferences.getInt("temperature_unit", 0)
        val windUnit = sharedPreferences.getInt("wind_speed_unit", 0)
        val refresh = sharedPreferences.getInt("refresh_time", 0)
        return Triple(tempUnit, windUnit, refresh)
    }


    fun saveFavoriteCities(cities: List<City>) {
        val json = gson.toJson(cities)
        sharedPreferences.edit().putString("favorite_cities_json", json).apply()
    }

    fun loadFavoriteCities(): List<City> {
        val json = sharedPreferences.getString("favorite_cities_json", null)
        return if (json != null) {
            val type = object : TypeToken<List<City>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    fun saveLastChosenCity(city: City, weatherModel: WeatherModel) {
        val json = gson.toJson(city)
        sharedPreferences.edit().putString("last_city", json).apply()

        val json2 = gson.toJson(weatherModel)
        sharedPreferences.edit().putString("weather", json2).apply()
    }

    fun loadLastCity(): City {
        val json = sharedPreferences.getString("last_city", null)
        return if (json != null) {
            val type = object : TypeToken<City>() {}.type
            gson.fromJson(json, type)
        } else {
            City(
                id = 1,
                name = "Warszawa",
                region = "",
                country = "Poland",
                lat = 52.2297,
                lon = 21.0122,
                url = ""
            )
        }
    }

    fun loadLastChosenCity(): WeatherModel? {
        val json = sharedPreferences.getString("weather", null)
        return if (json != null) {
            val type = object : TypeToken<WeatherModel>() {}.type
            gson.fromJson(json, type)
        } else {
            null
        }
    }

}