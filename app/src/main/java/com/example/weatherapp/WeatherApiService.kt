package com.example.weatherapp

import android.util.Log
import java.net.URL
import java.net.URLEncoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Handles calls to the OpenWeather API and returns results as raw JSON strings.
 */
class WeatherApiService(private val apiKey: String) {

    suspend fun getWeatherData(city: String): Result<String> = withContext(Dispatchers.IO) {
        val encodedCity = URLEncoder.encode(city.trim(), Charsets.UTF_8.name())
        val url = URL("$BASE_URL?q=$encodedCity&units=metric&appid=$apiKey")

        runCatching {
            val response = url.readText(Charsets.UTF_8)
            Log.d(TAG, "API response: $response")
            response
        }.onFailure { error ->
            Log.e(TAG, "API call failed", error)
        }
    }

    private companion object {
        private const val TAG = "WeatherApiService"
        private const val BASE_URL = "https://api.openweathermap.org/data/2.5/weather"
    }
}
