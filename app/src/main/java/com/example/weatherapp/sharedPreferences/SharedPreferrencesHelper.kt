import android.content.Context
import com.example.weatherapp.api.City
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

    fun saveFavoriteCity(city: City) {
        val current = getFavoriteCities().toMutableList()
        if (!current.any { it == city }) {
            current.add(city)
            saveFavoriteCities(current)
        }
    }

    fun removeFavoriteCity(city: City) {
        val current = getFavoriteCities().toMutableList()
        current.removeAll { it == city }
        saveFavoriteCities(current)
    }

    fun isCityFavorite(city: City): Boolean {
        return getFavoriteCities().any { it == city }
    }
}