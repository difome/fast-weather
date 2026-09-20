package com.difome.fast.data.model

import androidx.compose.runtime.Immutable

@Immutable
data class WeatherUI(
    val city: String,
    val temp: Int,
    val feelsLike: Int,
    val minTemp: Int,
    val maxTemp: Int,
    val conditionCode: Int = 0,
    val humidity: Int = 0,
    val windSpeed: Double = 0.0,
    val verbalSummary: String = "",
    val hourlyForecast: List<HourForecast> = emptyList(),
    val isNight: Boolean = false,
    val currentCityHour: Int = 12,
    val dailyForecast: List<DayForecast> = emptyList(),
    val pressure: Int = 101300,
    val cloudiness: Int = 0,
    val precipProbability: Int = 0,
    val precipAmount: Double = 0.0,
    val windDirection: String = "",
    val sunrise: String = "",
    val sunset: String = ""
)
