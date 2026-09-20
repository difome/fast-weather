package com.difome.fast.data.model

import androidx.compose.runtime.Immutable

@Immutable
data class DayForecast(
    val dateString: String,
    val dayOfWeek: String,
    val formattedDate: String,
    val minTemp: Int,
    val maxTemp: Int,
    val conditionCode: Int,
    val nightConditionCode: Int = conditionCode,
    val windSpeed: Double = 0.0,
    val humidity: Int = 0,
    val cloudiness: Int = 0,
    val precipProbability: Int = 0,
    val precipAmount: Double = 0.0,
    val windDirection: String = ""
)
