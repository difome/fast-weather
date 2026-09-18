package com.difome.fast.data.repository

import com.difome.fast.common.AppConstants
import com.difome.fast.data.model.WeatherUI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

suspend fun loadWeather(locationId: String = "krasnodar"): WeatherUI = withContext(Dispatchers.IO) {
    val connection = URL("${AppConstants.WEATHER_URL}/api/weather/location/forecast/by_id")
        .openConnection() as HttpURLConnection
    val body = JSONObject().apply {
        put("lang", "ukr")
        put("location_id", locationId)
        put("forecast_days", 10)
    }.toString()

    connection.requestMethod = "POST"
    connection.doOutput = true
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

    WeatherUI(
        city = city,
        temp = now.getInt("temp"),
        feelsLike = now.getInt("temp_feels"),
        minTemp = temp.getInt("min"),
        maxTemp = temp.getInt("max")
    )
}