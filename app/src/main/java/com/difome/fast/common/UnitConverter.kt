package com.difome.fast.common

import androidx.annotation.StringRes
import com.difome.fast.R

object UnitConverter {

    fun toFahrenheit(celsius: Int): Int {
        return (celsius * 9 / 5) + 32
    }

    fun toKmH(ms: Double): Double {
        return ms * 3.6
    }

    fun toMmHg(pressurePa: Int): Int {
        return (pressurePa * 0.00750062).toInt()
    }

    fun toMbar(pressurePa: Int): Int {
        return (pressurePa / 100)
    }

    @StringRes
    fun getWindDirectionRes(dir: String): Int? {
        return when (dir.trim().uppercase()) {
            "N" -> R.string.wind_dir_n
            "NE" -> R.string.wind_dir_ne
            "E" -> R.string.wind_dir_e
            "SE" -> R.string.wind_dir_se
            "S" -> R.string.wind_dir_s
            "SW" -> R.string.wind_dir_sw
            "W" -> R.string.wind_dir_w
            "NW" -> R.string.wind_dir_nw
            else -> null
        }
    }

    fun windDirectionToDegrees(dir: String): Float {
        return when (dir.trim().uppercase()) {
            "N" -> 0f
            "NNE" -> 22.5f
            "NE" -> 45f
            "ENE" -> 67.5f
            "E" -> 90f
            "ESE" -> 112.5f
            "SE" -> 135f
            "SSE" -> 157.5f
            "S" -> 180f
            "SSW" -> 202.5f
            "SW" -> 225f
            "WSW" -> 247.5f
            "W" -> 270f
            "WNW" -> 292.5f
            "NW" -> 315f
            "NNW" -> 337.5f
            else -> 0f
        }
    }
}
