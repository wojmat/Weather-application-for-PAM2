# Weather Application for PAM2

An Android weather app that lets users enter a city name and view the current weather conditions
powered by the OpenWeather API.

## Features
- Search by city name.
- Displays temperature, min/max temperature, wind, pressure, humidity, sunrise, and sunset.
- Loading and error states for improved feedback.

## Getting started
### 1) Configure your API key
Replace the value of `OPEN_WEATHER_API_KEY` in `app/build.gradle` with your OpenWeather API key.

### 2) Run the app
1. Open the project in Android Studio.
2. Sync Gradle.
3. Select a device or emulator and click **Run**.

## Documentation
- [Architecture](docs/ARCHITECTURE.md)
- [Usage](docs/USAGE.md)

## Project structure
```
app/src/main/java/com/example/weatherapp
├── MainActivity.kt
└── WeatherApiService.kt

app/src/main/res
├── layout/activity_main.xml
└── values/strings.xml
```

## Notes
- The API key in this repo is a placeholder; store secrets securely for production apps.
