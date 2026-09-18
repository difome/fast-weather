package com.difome.fast.data.repository

import androidx.appcompat.app.AppCompatDelegate
import com.difome.fast.common.AppConstants
import com.difome.fast.data.model.HourForecast
import com.difome.fast.data.model.LocationSuggestion
import com.difome.fast.data.model.WeatherUI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.Calendar
import java.util.Locale

suspend fun loadWeather(locationId: String = "krasnodar"): WeatherUI = withContext(Dispatchers.IO) {
    val connection = URL("${AppConstants.WEATHER_URL}/api/weather/location/forecast/by_id")
        .openConnection() as HttpURLConnection
    val currentLang = getSinoptikLanguage()
    val body = JSONObject().apply {

        put("lang", currentLang)
        put("location_id", locationId)
        put("forecast_days", 10)
    }.toString()

    connection.requestMethod = "POST"
    connection.doOutput = true
    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
    connection.setRequestProperty("Content-Type", "application/json")
    connection.setRequestProperty("Accept", "application/json")

    connection.outputStream.use {
        it.write(body.toByteArray(Charsets.UTF_8))
    }


    if (connection.responseCode !in 200..299) {
        throw Exception("HTTP ${connection.responseCode}")
    }

    val response = connection.inputStream
        .bufferedReader()
        .use { it.readText() }


    val json = JSONObject(response)

    val city = json
        .getJSONObject("location")
        .getString("title")

    val forecast = json.getJSONObject("forecast")
    val date = forecast.keys().next()
    val today = forecast.getJSONObject(date)

    val now = today.getJSONObject("now")
    val temp = today.getJSONObject("temp")

    val verbalSummary = today.optJSONObject("verbal")?.optString("gen", "") ?: ""

    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val hoursArray = today.optJSONArray("hours")
    
    var currentHourObj = hoursArray?.optJSONObject(0)
    if (hoursArray != null) {
        for (i in 0 until hoursArray.length()) {
            val item = hoursArray.optJSONObject(i)
            if (item != null && item.optInt("hour", -1) == currentHour) {
                currentHourObj = item
                break
            }
        }
    }

    val humidity = currentHourObj?.optInt("humidity", 0) ?: 0
    val windSpeed = currentHourObj?.optJSONObject("wind")?.optDouble("speed", 0.0) ?: 0.0
    val hourlyList = mutableListOf<HourForecast>()
    if (hoursArray != null) {
        for (i in 0 until hoursArray.length()) {
            val item = hoursArray.getJSONObject(i)
            hourlyList.add(
                HourForecast(
                    hour = item.getInt("hour"),
                    temp = item.getInt("temp"),
                    conditionCode = item.optInt("condition", 0)
                )
            )
        }
    }
    WeatherUI(
        city = city,
        temp = now.getInt("temp"),
        feelsLike = now.getInt("temp_feels"),
        minTemp = temp.getInt("min"),
        maxTemp = temp.getInt("max"),
        conditionCode = now.optInt("condition", 0),
        humidity = humidity,
        windSpeed = windSpeed,
        verbalSummary = verbalSummary,
        hourlyForecast = hourlyList
    )

}

suspend fun searchLocations(query: String): List<LocationSuggestion> = withContext(Dispatchers.IO) {
    if (query.trim().length < 2) return@withContext emptyList()

    val connection = URL("${AppConstants.WEATHER_URL}/api/search/suggest")
        .openConnection() as HttpURLConnection

    val currentLang = getSinoptikLanguage()
    val body = JSONObject().apply {
        put("query", query)
        put("lang", currentLang)
        put("limit", 30)
    }.toString()

    connection.requestMethod = "POST"
    connection.doOutput = true
    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
    connection.setRequestProperty("Content-Type", "application/json")
    connection.setRequestProperty("Accept", "application/json")

    connection.outputStream.use {
        it.write(body.toByteArray(Charsets.UTF_8))
    }

    if (connection.responseCode !in 200..299) {
        return@withContext emptyList()
    }

    val response = connection.inputStream.bufferedReader().use { it.readText() }
    val json = JSONObject(response)
    val locationsArray = json.optJSONArray("locations") ?: return@withContext emptyList()

    val ignoredTypes = setOf(101, 102, 103, 104)

    val resultList = mutableListOf<LocationSuggestion>()
    for (i in 0 until locationsArray.length()) {
        val item = locationsArray.getJSONObject(i)
        val type = item.optInt("type", 0)

        if (type in ignoredTypes) {
            continue
        }

        resultList.add(
            LocationSuggestion(
                id = item.getString("id"),
                title = item.getString("title"),
                description = item.optString("description", "")
            )
        )
    }

    resultList
}

private fun getSinoptikLanguage(): String {
    val appLocale = AppCompatDelegate.getApplicationLocales().get(0)
        ?: Locale.getDefault()

    return when (appLocale.language) {
        "ru" -> "rus"
        "en" -> "eng"
        "uk" -> "ukr"
        else -> "rus"
    }
}