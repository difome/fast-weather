package com.difome.fast.data.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.difome.fast.R

object WeatherConditionHelper {

    private val THUNDER_CODES = setOf(140, 141, 142, 240, 241, 242, 340, 341, 342, 440, 441, 442)
    private val RAIN_SUN_CODES = setOf(110, 120, 130, 210, 220, 230, 310, 320, 330)
    private val RAIN_OVERCAST_CODES = setOf(410, 420, 430)
    private val SNOW_CODES = setOf(
        103, 111, 112, 121, 122, 131, 132, 211, 212, 221, 222, 231, 232,
        311, 312, 321, 322, 331, 332, 411, 412, 421, 422, 431, 432
    )

    @Composable
    fun getIcon(code: Int, isNight: Boolean = false): ImageVector {
        return when (code) {
            in THUNDER_CODES -> ImageVector.vectorResource(id = R.drawable.ic_weather_thunder)
            in RAIN_SUN_CODES -> ImageVector.vectorResource(id = R.drawable.ic_weather_rain_sun)
            in RAIN_OVERCAST_CODES -> ImageVector.vectorResource(id = R.drawable.ic_weather_rain)
            in SNOW_CODES -> ImageVector.vectorResource(id = R.drawable.ic_weather_snow)
            600 -> ImageVector.vectorResource(id = R.drawable.ic_weather_fog)
            in 400..409 -> ImageVector.vectorResource(id = R.drawable.ic_weather_cloud)
            in 100..399, 500 -> if (isNight) ImageVector.vectorResource(id = R.drawable.ic_weather_partly_cloudy_night) else ImageVector.vectorResource(id = R.drawable.ic_weather_partly_cloudy_day)
            0 -> if (isNight) ImageVector.vectorResource(id = R.drawable.ic_weather_night) else ImageVector.vectorResource(id = R.drawable.ic_weather_sun)
            else -> if (isNight) ImageVector.vectorResource(id = R.drawable.ic_weather_night) else ImageVector.vectorResource(id = R.drawable.ic_weather_sun)
        }
    }

    @StringRes
    fun getTitleRes(code: Int): Int {
        return when (code) {
            0 -> R.string.condition_0
            100 -> R.string.condition_100
            103 -> R.string.condition_103
            110 -> R.string.condition_110
            111 -> R.string.condition_111
            112 -> R.string.condition_112
            120 -> R.string.condition_120
            121 -> R.string.condition_121
            122 -> R.string.condition_122
            130 -> R.string.condition_130
            131 -> R.string.condition_131
            132 -> R.string.condition_132
            140 -> R.string.condition_140
            141 -> R.string.condition_141
            142 -> R.string.condition_142
            200 -> R.string.condition_200
            210 -> R.string.condition_210
            211 -> R.string.condition_211
            212 -> R.string.condition_212
            220 -> R.string.condition_220
            221 -> R.string.condition_221
            222 -> R.string.condition_222
            230 -> R.string.condition_230
            231 -> R.string.condition_231
            232 -> R.string.condition_232
            240 -> R.string.condition_240
            241 -> R.string.condition_241
            242 -> R.string.condition_242
            300 -> R.string.condition_300
            310 -> R.string.condition_310
            311 -> R.string.condition_311
            312 -> R.string.condition_312
            320 -> R.string.condition_320
            321 -> R.string.condition_321
            322 -> R.string.condition_322
            330 -> R.string.condition_330
            331 -> R.string.condition_331
            332 -> R.string.condition_332
            400 -> R.string.condition_400
            410 -> R.string.condition_410
            411 -> R.string.condition_411
            412 -> R.string.condition_412
            420 -> R.string.condition_420
            421 -> R.string.condition_421
            422 -> R.string.condition_422
            430 -> R.string.condition_430
            431 -> R.string.condition_431
            432 -> R.string.condition_432
            440 -> R.string.condition_440
            441 -> R.string.condition_441
            442 -> R.string.condition_442
            500 -> R.string.condition_500
            600 -> R.string.condition_600
            else -> R.string.condition_0
        }
    }
}
