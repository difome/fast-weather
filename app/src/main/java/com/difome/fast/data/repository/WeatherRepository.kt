package com.difome.fast.data.repository

import com.difome.fast.common.AppConstants
import com.difome.fast.data.model.DayForecast
import com.difome.fast.data.model.HourForecast
import com.difome.fast.data.model.LocationSuggestion
import com.difome.fast.data.model.WeatherUI
import com.difome.fast.data.remote.dto.HourDto
import com.difome.fast.data.remote.dto.SearchSuggestResponseDto
import com.difome.fast.data.remote.dto.WeatherResponseDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.math.abs

private val jsonParser = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
    isLenient = true
}

suspend fun loadWeather(locationId: String = AppConstants.defaultCityId): WeatherUI = withContext(Dispatchers.IO) {
    val connection = URL("${AppConstants.WEATHER_URL}/api/weather/location/forecast/by_id")
        .openConnection() as HttpURLConnection
    try {
        val currentLang = getSinoptikLanguage()
        val body = "{\"lang\":\"$currentLang\",\"location_id\":\"$locationId\",\"forecast_days\":10}"

        connection.requestMethod = "POST"
        connection.doOutput = true
        connection.connectTimeout = 10000
        connection.readTimeout = 10000
        connection.setRequestProperty(AppConstants.HEADER_USER_AGENT, AppConstants.USER_AGENT_VALUE)
        connection.setRequestProperty(AppConstants.HEADER_CONTENT_TYPE, AppConstants.CONTENT_TYPE_JSON)
        connection.setRequestProperty(AppConstants.HEADER_ACCEPT, AppConstants.CONTENT_TYPE_JSON)
        connection.setRequestProperty("Referer", AppConstants.WEATHER_URL)
        connection.setRequestProperty("Origin", AppConstants.WEATHER_URL)

        connection.outputStream.use {
            it.write(body.toByteArray(Charsets.UTF_8))
        }

        if (connection.responseCode !in 200..299) {
            throw Exception("HTTP ${connection.responseCode}")
        }

        val responseString = connection.inputStream.bufferedReader().use { it.readText() }
        val dto = jsonParser.decodeFromString<WeatherResponseDto>(responseString)

        val city = dto.location?.title.orEmpty()
        val sortedDateKeys = dto.forecast.keys.sorted()
        if (sortedDateKeys.isEmpty()) throw Exception("Empty forecast data")

        val todayKey = sortedDateKeys.firstOrNull { key ->
            dto.forecast[key]?.now != null
        } ?: sortedDateKeys.first()

        val todayDto = dto.forecast[todayKey] ?: throw Exception("No today data")
        val nowDto = todayDto.now
        val tempDto = todayDto.temp

        val verbalSummary = todayDto.verbal?.gen.orEmpty()

        val risesAt = todayDto.sun?.risesAt ?: "06:08"
        val setsAt = todayDto.sun?.setsAt ?: "18:25"
        val riseHour = risesAt.split(":").firstOrNull()?.toIntOrNull() ?: 6
        val setHour = setsAt.split(":").firstOrNull()?.toIntOrNull() ?: 18

        val tzOffsetSeconds = nowDto?.currentUtcTzOffset ?: 0

        val cityLocalHour = Calendar.getInstance(TimeZone.getTimeZone("UTC")).run {
            add(Calendar.SECOND, tzOffsetSeconds)
            get(Calendar.HOUR_OF_DAY)
        }

        val isNight = cityLocalHour < riseHour || cityLocalHour >= setHour

        val hoursList = todayDto.hours

        var currentHourDto: HourDto? = null
        var minHourDiff = Int.MAX_VALUE

        for (h in hoursList) {
            val hVal = h.hour ?: continue
            val diff = abs(hVal - cityLocalHour)
            if (diff < minHourDiff) {
                minHourDiff = diff
                currentHourDto = h
            }
        }

        val humidity = currentHourDto?.humidity ?: 0
        val windSpeed = currentHourDto?.wind?.speed ?: 0.0
        val windDir = currentHourDto?.wind?.dir.orEmpty()
        val pressure = currentHourDto?.pressure ?: AppConstants.DEFAULT_PRESSURE_PA
        val cloudiness = currentHourDto?.cloudiness ?: 0
        val precipProb = currentHourDto?.precip ?: 0
        val precipAmt = currentHourDto?.precipAmount ?: 0.0

        val hourlyList = hoursList.map { item ->
            val hVal = item.hour ?: 0
            HourForecast(
                hour = hVal,
                temp = item.temp ?: 0,
                conditionCode = item.condition ?: 0,
                isNight = hVal < riseHour || hVal >= setHour,
                windSpeed = item.wind?.speed ?: 0.0,
                windDirection = item.wind?.dir.orEmpty(),
                precipProbability = item.precip ?: 0
            )
        }

        val dailyList = sortedDateKeys.mapNotNull { dateKey ->
            val dayDto = dto.forecast[dateKey] ?: return@mapNotNull null
            val dayTempDto = dayDto.temp ?: return@mapNotNull null

            val dayMin = dayTempDto.min ?: 0
            val dayMax = dayTempDto.max ?: 0
            val dayCond = dayDto.condition ?: 0

            val hList = dayDto.hours
            val nightCond = hList.firstOrNull()?.condition ?: dayCond

            var noonDto: HourDto? = null
            if (hList.isNotEmpty()) {
                var minNoonDiff = Int.MAX_VALUE
                for (h in hList) {
                    val hVal = h.hour ?: continue
                    val diff = abs(hVal - 12)
                    if (diff < minNoonDiff) {
                        minNoonDiff = diff
                        noonDto = h
                    }
                }
            }

            var dayPrecipProb = dayDto.precip ?: 0
            var dayPrecipAmt = dayDto.precipAmount ?: 0.0
            var dayCloudiness = noonDto?.cloudiness ?: 0

            for (h in hList) {
                val hp = h.precip ?: 0
                if (hp > dayPrecipProb) dayPrecipProb = hp

                val hpa = h.precipAmount ?: 0.0
                if (hpa > dayPrecipAmt) dayPrecipAmt = hpa

                val hc = h.cloudiness ?: 0
                if (hc > dayCloudiness) dayCloudiness = hc
            }

            val dayWindDir = noonDto?.wind?.dir.orEmpty()
            val dayWind = noonDto?.wind?.speed ?: 0.0
            val dayHumidity = noonDto?.humidity ?: 0

            val (dayOfWeek, formattedDate) = parseDateString(dateKey)

            DayForecast(
                dateString = dateKey,
                dayOfWeek = dayOfWeek,
                formattedDate = formattedDate,
                minTemp = dayMin,
                maxTemp = dayMax,
                conditionCode = dayCond,
                nightConditionCode = nightCond,
                windSpeed = dayWind,
                humidity = dayHumidity,
                cloudiness = dayCloudiness,
                precipProbability = dayPrecipProb,
                precipAmount = dayPrecipAmt,
                windDirection = dayWindDir
            )
        }

        val currentTemp = nowDto?.temp ?: 0
        val currentFeelsLike = nowDto?.tempFeels ?: currentTemp
        val minTemp = tempDto?.min ?: 0
        val maxTemp = tempDto?.max ?: 0
        val conditionCode = nowDto?.condition ?: 0

        WeatherUI(
            city = city,
            temp = currentTemp,
            feelsLike = currentFeelsLike,
            minTemp = minTemp,
            maxTemp = maxTemp,
            conditionCode = conditionCode,
            humidity = humidity,
            windSpeed = windSpeed,
            verbalSummary = verbalSummary,
            hourlyForecast = hourlyList,
            isNight = isNight,
            currentCityHour = cityLocalHour,
            dailyForecast = dailyList,
            pressure = pressure,
            cloudiness = cloudiness,
            precipProbability = precipProb,
            precipAmount = precipAmt,
            windDirection = windDir,
            sunrise = risesAt,
            sunset = setsAt
        )
    } finally {
        connection.disconnect()
    }
}

private fun parseDateString(dateStr: String): Pair<String, String> {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val date = sdf.parse(dateStr) ?: return Pair("", dateStr)
        val lang = getSinoptikLanguage()
        val locale = when {
            lang.startsWith("uk") -> Locale.forLanguageTag("uk")
            lang.startsWith("ru") -> Locale.forLanguageTag("ru")
            else -> Locale.ENGLISH
        }
        val dayOfWeekFormat = SimpleDateFormat("E", locale)
        val dateFormat = SimpleDateFormat("dd.MM", locale)
        val dow = dayOfWeekFormat.format(date).replaceFirstChar { it.uppercase() }
        val formatted = dateFormat.format(date)
        Pair(dow, formatted)
    } catch (_: Exception) {
        Pair("", dateStr)
    }
}

suspend fun searchLocations(query: String): List<LocationSuggestion> = withContext(Dispatchers.IO) {
    val result: List<LocationSuggestion> = if (query.trim().length < 2) {
        emptyList()
    } else {
        val connection = URL("${AppConstants.WEATHER_URL}/api/search/suggest")
            .openConnection() as HttpURLConnection

        try {
            val currentLang = getSinoptikLanguage()
            val body = "{\"query\":\"$query\",\"lang\":\"$currentLang\",\"limit\":30}"

            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.setRequestProperty(AppConstants.HEADER_USER_AGENT, AppConstants.USER_AGENT_VALUE)
            connection.setRequestProperty(AppConstants.HEADER_CONTENT_TYPE, AppConstants.CONTENT_TYPE_JSON)
            connection.setRequestProperty(AppConstants.HEADER_ACCEPT, AppConstants.CONTENT_TYPE_JSON)
            connection.setRequestProperty("Referer", AppConstants.WEATHER_URL)
            connection.setRequestProperty("Origin", AppConstants.WEATHER_URL)

            connection.outputStream.use {
                it.write(body.toByteArray(Charsets.UTF_8))
            }

            if (connection.responseCode !in 200..299) {
                emptyList()
            } else {
                val responseString = connection.inputStream.bufferedReader().use { it.readText() }
                val searchDto = jsonParser.decodeFromString<SearchSuggestResponseDto>(responseString)
                val list = searchDto.locations.ifEmpty { searchDto.list }

                list.mapNotNull { item ->
                    val id = item.id.orEmpty()
                    val title = item.title.orEmpty()
                    val description = item.description.orEmpty()
                    val type = item.type ?: -1

                    if (type in AppConstants.IGNORED_LOCATION_TYPES || id.isEmpty() || title.isEmpty()) {
                        null
                    } else {
                        LocationSuggestion(id = id, title = title, description = description)
                    }
                }
            }
        } finally {
            connection.disconnect()
        }
    }
    result
}

fun getSinoptikLanguage(): String {
    val sysLanguage = Locale.getDefault().language
    return when {
        sysLanguage.startsWith(AppConstants.LANG_UK) -> AppConstants.SINOPTIK_LANG_UKR
        sysLanguage.startsWith(AppConstants.LANG_RU) -> AppConstants.SINOPTIK_LANG_RUS
        else -> AppConstants.SINOPTIK_LANG_ENG
    }
}
