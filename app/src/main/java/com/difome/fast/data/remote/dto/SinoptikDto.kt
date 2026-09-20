package com.difome.fast.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponseDto(
    val location: LocationDto? = null,
    val forecast: Map<String, DayForecastDto> = emptyMap()
)

@Serializable
data class LocationDto(
    val id: String? = null,
    val title: String? = null,
    val description: String? = null,
    val type: Int? = null
)

@Serializable
data class DayForecastDto(
    val now: NowDto? = null,
    val condition: Int? = null,
    val temp: TempRangeDto? = null,
    val sun: SunDto? = null,
    val verbal: VerbalDto? = null,
    val hours: List<HourDto> = emptyList(),
    val precip: Int? = null,
    @SerialName("precip_amount") val precipAmount: Double? = null
)

@Serializable
data class NowDto(
    val temp: Int? = null,
    @SerialName("temp_feels") val tempFeels: Int? = null,
    val condition: Int? = null,
    @SerialName("current_utc_tz_offset") val currentUtcTzOffset: Int? = null
)

@Serializable
data class TempRangeDto(
    val min: Int? = null,
    val max: Int? = null
)

@Serializable
data class SunDto(
    @SerialName("rises_at") val risesAt: String? = null,
    @SerialName("sets_at") val setsAt: String? = null
)

@Serializable
data class VerbalDto(
    val gen: String? = null,
    val folk: String? = null
)

@Serializable
data class HourDto(
    val hour: Int? = null,
    val temp: Int? = null,
    @SerialName("temp_feels") val tempFeels: Int? = null,
    val condition: Int? = null,
    val pressure: Int? = null,
    val humidity: Int? = null,
    val cloudiness: Int? = null,
    val precip: Int? = null,
    @SerialName("precip_amount") val precipAmount: Double? = null,
    val wind: WindDto? = null
)

@Serializable
data class WindDto(
    val dir: String? = null,
    val speed: Double? = null
)

@Serializable
data class SearchSuggestResponseDto(
    val locations: List<LocationDto> = emptyList(),
    val list: List<LocationDto> = emptyList()
)
