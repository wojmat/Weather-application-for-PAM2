package com.example.weatherapp

import WeatherApiService
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import android.util.Log

// This is the main activity class for the application, extending AppCompatActivity.
class MainActivity : AppCompatActivity() {

    private val apiService = WeatherApiService("69099485ea4553bae8fc0e52841ce693")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnFetchWeather = findViewById<Button>(R.id.btnFetchWeather)
        btnFetchWeather.setOnClickListener {
            val cityInput = findViewById<EditText>(R.id.cityInput).text.toString()
            if (cityInput.isNotEmpty()) {
                fetchWeatherData(cityInput)
            } else {
                Toast.makeText(this, "Please enter a city name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // This function initiates a coroutine to fetch weather data asynchronously.
    private fun fetchWeatherData(city: String) {
        // Launching a coroutine on the IO dispatcher for network operations.
        CoroutineScope(Dispatchers.IO).launch {
            val data = async { apiService.getWeatherData(city) }.await()
            // If data is not null, update the UI with the received data.
            if (data != null) {
                updateUI(data)
            } else {
                  // withContext(Dispatchers.Main) {
                  // findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
                // Logging an error if no data is received from the API.
                Log.e("API_ERROR", "No data received from API")

            }
        }
    }

    // This function updates the UI with the result of the weather data.
    private suspend fun updateUI(result: String) = withContext(Dispatchers.Main) {
        try {
            // The commented out code here would parse the JSON result and update UI elements accordingly.
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

                val updatedAt:Long = jsonObj.getLong("dt")
                val updatedAtText = "Updated at: "+ SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(Date(updatedAt*1000))
                val temp = main.getString("temp")+"°C"
                val tempMin = "Min Temp: " + main.getString("temp_min")+"°C"
                val tempMax = "Max Temp: " + main.getString("temp_max")+"°C"
                val pressure = main.getString("pressure")
                val humidity = main.getString("humidity")

                val sunrise:Long = sys.getLong("sunrise")
                val sunset:Long = sys.getLong("sunset")
                val windSpeed = wind.getString("speed")
                val weatherDescription = weather.getString("description")

                val address = jsonObj.getString("name")+", "+sys.getString("country")

                /* Populating extracted data into our views */
                findViewById<TextView>(R.id.address).text = address
                findViewById<TextView>(R.id.updated_at).text =  updatedAtText
                findViewById<TextView>(R.id.status).text = weatherDescription.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                }
                findViewById<TextView>(R.id.temp).text = temp
                findViewById<TextView>(R.id.temp_min).text = tempMin
                findViewById<TextView>(R.id.temp_max).text = tempMax
                findViewById<TextView>(R.id.sunrise).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise*1000))
                findViewById<TextView>(R.id.sunset).text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset*1000))
                findViewById<TextView>(R.id.wind).text = windSpeed
                findViewById<TextView>(R.id.pressure).text = pressure
                findViewById<TextView>(R.id.humidity).text = humidity

                /* Views populated, Hiding the loader, Showing the main design */
            findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE

            // Logging the received data for debugging.
            Log.d("API_RESULT", "Received data: $result")


        } catch (e: Exception) {
            // Logging an error if there is an issue processing the result.
            Log.e("API_ERROR", "Error processing result: ${e.message}")

            findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
            findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
        }
    }
}
