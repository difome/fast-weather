package com.difome.fast.common

import androidx.appcompat.app.AppCompatDelegate
import java.util.Locale

object AppConstants {
    const val WEATHER_URL = "https://sinoptik.ua"

    val defaultCityId: String
        get() {
            val appLanguage = AppCompatDelegate.getApplicationLocales().get(0)?.language
                ?: Locale.getDefault().language

            return when (appLanguage) {
                "uk" -> "kyiv"
                "en" -> "london"
                "ru" -> "krasnodar"
                else -> "london"
            }
        }
}