package com.example.weatherapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import android.util.Log
import kotlinx.coroutines.launch

/**
 * Main screen that lets users search by city and view the current weather.
 */
class MainActivity : AppCompatActivity() {

    private val apiService = WeatherApiService(BuildConfig.OPEN_WEATHER_API_KEY)

    private lateinit var cityInput: EditText
    private lateinit var loader: ProgressBar
    private lateinit var mainContainer: RelativeLayout
    private lateinit var errorText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()

        val btnFetchWeather = findViewById<Button>(R.id.btnFetchWeather)
        btnFetchWeather.setOnClickListener {
            val cityName = cityInput.text.toString()
            if (cityName.isNotBlank()) {
                fetchWeatherData(cityName)
            } else {
                Toast.makeText(this, getString(R.string.error_enter_city), Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Captures reusable views once, keeping view lookups consistent and in one place.
     */
    private fun bindViews() {
        cityInput = findViewById(R.id.cityInput)
        loader = findViewById(R.id.loader)
        mainContainer = findViewById(R.id.mainContainer)
        errorText = findViewById(R.id.errorText)
    }

    /**
     * Fetches weather data asynchronously and updates the UI based on the response.
     */
    private fun fetchWeatherData(city: String) {
        setLoadingState(isLoading = true)
        lifecycleScope.launch {
            apiService.getWeatherData(city)
                .onSuccess { updateUI(it) }
                .onFailure { error ->
                    Log.e(TAG, "No data received from API", error)
                    showError(getString(R.string.error_fetch_weather))
                }
        }
    }

    /**
     * Parses the API response and populates all weather fields.
     */
    private fun updateUI(result: String) {
        try {
            val jsonObj = JSONObject(result)
            val main = jsonObj.getJSONObject("main")
            val sys = jsonObj.getJSONObject("sys")
            val wind = jsonObj.getJSONObject("wind")
            val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

            val updatedAt = jsonObj.getLong("dt")
            val updatedAtText = getString(
                R.string.updated_at,
                SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(Date(updatedAt * 1000))
            )
            val temp = getString(R.string.temperature_celsius, main.getString("temp"))
            val tempMin = getString(R.string.temperature_min, main.getString("temp_min"))
            val tempMax = getString(R.string.temperature_max, main.getString("temp_max"))
            val pressure = main.getString("pressure")
            val humidity = main.getString("humidity")

            val sunrise = sys.getLong("sunrise")
            val sunset = sys.getLong("sunset")
            val windSpeed = wind.getString("speed")
            val weatherDescription = weather.getString("description")

            val address = getString(
                R.string.location_format,
                jsonObj.getString("name"),
                sys.getString("country")
            )

            findViewById<TextView>(R.id.address).text = address
            findViewById<TextView>(R.id.updated_at).text = updatedAtText
            findViewById<TextView>(R.id.status).text = weatherDescription.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }
            findViewById<TextView>(R.id.temp).text = temp
            findViewById<TextView>(R.id.temp_min).text = tempMin
            findViewById<TextView>(R.id.temp_max).text = tempMax
            findViewById<TextView>(R.id.sunrise).text = formatTime(sunrise)
            findViewById<TextView>(R.id.sunset).text = formatTime(sunset)
            findViewById<TextView>(R.id.wind).text = windSpeed
            findViewById<TextView>(R.id.pressure).text = pressure
            findViewById<TextView>(R.id.humidity).text = humidity

            setLoadingState(isLoading = false)

            Log.d("API_RESULT", "Received data: $result")
        } catch (e: Exception) {
            Log.e("API_ERROR", "Error processing result", e)
            showError(getString(R.string.error_parse_weather))
        }
    }

    private fun formatTime(epochSeconds: Long): String {
        return SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(epochSeconds * 1000))
    }

    private fun setLoadingState(isLoading: Boolean) {
        loader.visibility = if (isLoading) View.VISIBLE else View.GONE
        mainContainer.visibility = if (isLoading) View.GONE else View.VISIBLE
        errorText.visibility = View.GONE
    }

    private fun showError(message: String) {
        loader.visibility = View.GONE
        mainContainer.visibility = View.GONE
        errorText.text = message
        errorText.visibility = View.VISIBLE
    }

    private companion object {
        private const val TAG = "MainActivity"
    }
}
