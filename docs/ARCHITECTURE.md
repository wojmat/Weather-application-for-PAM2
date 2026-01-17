# Architecture

## Overview
The app is a single-screen Android application that requests current weather data from the
OpenWeather API and renders the results in `MainActivity`.

## High-level flow
1. The user enters a city name and taps **Get Weather**.
2. `MainActivity` validates input, shows the loading state, and calls `WeatherApiService`.
3. `WeatherApiService` performs the HTTP request on the IO dispatcher and returns a `Result`.
4. `MainActivity` parses the JSON response and updates the UI or shows an error state.

## Key components
- **MainActivity**: Owns UI state, input validation, and JSON parsing.
- **WeatherApiService**: Builds the API URL, performs the network call, and logs failures.
- **Resources**: Strings and layouts live in `app/src/main/res` to keep the UI text centralized.

## Error handling
- Network errors return a failed `Result` from the service and surface a friendly message.
- Parsing errors are caught in `MainActivity`, which switches to an error state.

## Configuration
- The OpenWeather API key is injected via `BuildConfig.OPEN_WEATHER_API_KEY`.
  Update this value in `app/build.gradle` when configuring a new key.
