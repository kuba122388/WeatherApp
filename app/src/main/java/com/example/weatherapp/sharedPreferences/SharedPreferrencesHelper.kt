import android.content.Context
import com.example.weatherapp.api.City
import com.example.weatherapp.api.WeatherModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferencesHelper(context: Context) {

    private val sharedPreferences = context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()


    fun saveFavoriteCities(cities: List<City>) {
        val json = gson.toJson(cities)
        sharedPreferences.edit().putString("favorite_cities_json", json).apply()
    }

    fun getFavoriteCities(): List<City> {
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

    fun loadLastCity(): City? {
        val json = sharedPreferences.getString("last_city", null)
        return if (json != null) {
            val type = object : TypeToken<City>() {}.type
            gson.fromJson(json, type)
        } else {
            null
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