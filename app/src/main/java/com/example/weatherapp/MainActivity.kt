package com.example.weatherapp

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

    // These are private constants for the city and API key used for weather data requests. This is likely to be changed as we consider using an edit text in layout and search for a city.
    private val city = "london,gb"
    private val api = "69099485ea4553bae8fc0e52841ce693"

    // This is the onCreate method, which is called when the activity is created.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This line would set the content view to the layout defined in activity_main.xml (currently commented out). Now we focus on testing the API calls.
        setContentView(R.layout.activity_main)
        fetchWeatherData(city)
    }

    // This function initiates a coroutine to fetch weather data asynchronously.
    private fun fetchWeatherData(city: String) {
        // Launching a coroutine on the IO dispatcher for network operations.
        CoroutineScope(Dispatchers.IO).launch {
            // Asynchronously getting the weather data and waiting for the result.
            val data = async { getWeatherData(city) }.await()
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

    // This function makes a network request to get weather data.
    private fun getWeatherData(city: String): String? {
        return try {
            // Building the URL with the city and API key, and reading the response as a String.
            val response = URL("https://api.openweathermap.org/data/2.5/weather?q=$city&units=metric&appid=$api").readText(Charsets.UTF_8)
            // Logging the API response for debugging.
            Log.d("API_CALL", "API response: $response")
            response
        } catch (e: Exception) {
            // Logging an error if the API call fails.
            Log.e("API_CALL", "API call failed: ${e.message}")
            null
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
