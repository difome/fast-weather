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
import java.util.TimeZone

suspend fun loadWeather(locationId: String = AppConstants.defaultCityId): WeatherUI = withContext(Dispatchers.IO) {
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
    connection.setRequestProperty(AppConstants.HEADER_USER_AGENT, AppConstants.USER_AGENT_VALUE)
    connection.setRequestProperty(AppConstants.HEADER_CONTENT_TYPE, AppConstants.CONTENT_TYPE_JSON)
    connection.setRequestProperty(AppConstants.HEADER_ACCEPT, AppConstants.CONTENT_TYPE_JSON)

    connection.outputStream.use {
        it.write(body.toByteArray(Charsets.UTF_8))
    }

    if (connection.responseCode !in 200..299) {
        throw Exception("HTTP ${connection.responseCode}")
    }

    val response = connection.inputStream.bufferedReader().use { it.readText() }
    val json = JSONObject(response)

    val locationObj = json.getJSONObject("location")
    val city = locationObj.getString("title")

    val forecast = json.getJSONObject("forecast")
    val date = forecast.keys().next()
    val today = forecast.getJSONObject(date)

    val now = today.getJSONObject("now")
    val temp = today.getJSONObject("temp")

    val verbalSummary = today.optJSONObject("verbal")?.optString("gen", "") ?: ""

    val sunObj = today.getJSONObject("sun")
    val riseHour = sunObj.getString("rises_at").split(":").first().toInt()
    val setHour = sunObj.getString("sets_at").split(":").first().toInt()

    val tzOffsetSeconds = now.getInt("current_utc_tz_offset")
    val cityLocalHour = Calendar.getInstance(TimeZone.getTimeZone("UTC")).run {
        add(Calendar.SECOND, tzOffsetSeconds)
        get(Calendar.HOUR_OF_DAY)
    }

    val isNight = cityLocalHour < riseHour || cityLocalHour >= setHour

    val hoursArray = today.getJSONArray("hours")

    var currentHourObj = hoursArray.getJSONObject(0)
    for (i in 0 until hoursArray.length()) {
        val item = hoursArray.getJSONObject(i)
        if (item.getInt("hour") == cityLocalHour) {
            currentHourObj = item
            break
        }
    }

    val humidity = currentHourObj.optInt("humidity", 0)
    val windSpeed = currentHourObj.optJSONObject("wind")?.optDouble("speed", 0.0) ?: 0.0

    val hourlyList = mutableListOf<HourForecast>()
    for (i in 0 until hoursArray.length()) {
        val item = hoursArray.getJSONObject(i)
        val hourVal = item.getInt("hour")
        val isHourNight = hourVal < riseHour || hourVal >= setHour

        hourlyList.add(
            HourForecast(
                hour = hourVal,
                temp = item.getInt("temp"),
                conditionCode = item.getInt("condition"),
                isNight = isHourNight
            )
        )
    }

    WeatherUI(
        city = city,
        temp = now.getInt("temp"),
        feelsLike = now.getInt("temp_feels"),
        minTemp = temp.getInt("min"),
        maxTemp = temp.getInt("max"),
        conditionCode = now.getInt("condition"),
        humidity = humidity,
        windSpeed = windSpeed,
        verbalSummary = verbalSummary,
        hourlyForecast = hourlyList,
        isNight = isNight,
        currentCityHour = cityLocalHour
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
    connection.setRequestProperty(AppConstants.HEADER_USER_AGENT, AppConstants.USER_AGENT_VALUE)
    connection.setRequestProperty(AppConstants.HEADER_CONTENT_TYPE, AppConstants.CONTENT_TYPE_JSON)
    connection.setRequestProperty(AppConstants.HEADER_ACCEPT, AppConstants.CONTENT_TYPE_JSON)

    connection.outputStream.use {
        it.write(body.toByteArray(Charsets.UTF_8))
    }

    if (connection.responseCode !in 200..299) {
        return@withContext emptyList()
    }

    val response = connection.inputStream.bufferedReader().use { it.readText() }
    val json = JSONObject(response)
    val locationsArray = json.optJSONArray("locations") ?: return@withContext emptyList()

    val resultList = mutableListOf<LocationSuggestion>()
    for (i in 0 until locationsArray.length()) {
        val item = locationsArray.getJSONObject(i)
        val type = item.optInt("type", 0)

        if (type in AppConstants.IGNORED_LOCATION_TYPES) {
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

internal fun getSinoptikLanguage(): String {
    val appLocale = AppCompatDelegate.getApplicationLocales().get(0)
    val lang = if (appLocale != null && !appLocale.language.isNullOrEmpty()) {
        appLocale.language
    } else {
        Locale.getDefault().language
    }

    return when (lang) {
        AppConstants.LANG_RU -> AppConstants.SINOPTIK_LANG_RUS
        AppConstants.LANG_EN -> AppConstants.SINOPTIK_LANG_ENG
        AppConstants.LANG_UK -> AppConstants.SINOPTIK_LANG_UKR
        else -> AppConstants.SINOPTIK_LANG_RUS
    }
}
