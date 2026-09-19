package com.difome.fast.common

import androidx.appcompat.app.AppCompatDelegate
import java.util.Locale

object AppConstants {
    const val WEATHER_URL = "https://sinoptik.ua"

    const val LANG_UK = "uk"
    const val LANG_RU = "ru"
    const val LANG_EN = "en"

    const val SINOPTIK_LANG_UKR = "ukr"
    const val SINOPTIK_LANG_RUS = "rus"
    const val SINOPTIK_LANG_ENG = "eng"

    const val WIND_UNIT_MS = "ms"
    const val WIND_UNIT_KMH = "kmh"
    const val PRESSURE_UNIT_MBAR = "mbar"
    const val PRESSURE_UNIT_MMHG = "mmhg"

    const val DEFAULT_CITY_UK = "kyiv"
    const val DEFAULT_CITY_EN = "london"
    const val DEFAULT_CITY_RU = "krasnodar"

    const val HEADER_USER_AGENT = "User-Agent"
    const val USER_AGENT_VALUE = "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"
    const val HEADER_CONTENT_TYPE = "Content-Type"
    const val HEADER_ACCEPT = "Accept"
    const val CONTENT_TYPE_JSON = "application/json"

    val IGNORED_LOCATION_TYPES = setOf(101, 102, 103, 104)

    val defaultCityId: String
        get() {
            val appLanguage = AppCompatDelegate.getApplicationLocales().get(0)?.language
                ?: Locale.getDefault().language

            return when (appLanguage) {
                LANG_UK -> DEFAULT_CITY_UK
                LANG_EN -> DEFAULT_CITY_EN
                LANG_RU -> DEFAULT_CITY_RU
                else -> DEFAULT_CITY_EN
            }
        }
}
