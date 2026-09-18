package com.difome.fast.data.model

data class WeatherUI(
    val city: String,
    val temp: Int,
    val feelsLike: Int,
    val minTemp: Int,
    val maxTemp: Int,
    val conditionCode: Int = 0,
    val humidity: Int = 0,
    val windSpeed: Double = 0.0,
    val verbalSummary: String = ""
)
