package com.difome.fast.data.model

data class HourForecast(
    val hour: Int,
    val temp: Int,
    val conditionCode: Int,
    val isNight: Boolean = false,
    val windSpeed: Double = 0.0,
    val windDirection: String = "",
    val precipProbability: Int = 0
)
