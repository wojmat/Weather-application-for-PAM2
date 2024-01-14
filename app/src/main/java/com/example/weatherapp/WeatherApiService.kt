import android.util.Log
import java.net.URL

class WeatherApiService(private val apiKey: String) {

    fun getWeatherData(city: String): String? {
        return try {
            val response = URL("https://api.openweathermap.org/data/2.5/weather?q=$city&units=metric&appid=$apiKey").readText(Charsets.UTF_8)
            Log.d("API_CALL", "API response: $response")
            response
        } catch (e: Exception) {
            Log.e("API_CALL", "API call failed: ${e.message}")
            null
        }
    }
}
