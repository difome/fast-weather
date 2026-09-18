package com.difome.fast.data.model

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Dehaze
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector
import com.difome.fast.R

object WeatherConditionHelper {

    fun getIcon(code: Int): ImageVector {
        return when (code ){
             0 -> Icons.Default.WbSunny
             in 100..199 -> Icons.Default.WbCloudy
             in 200..299 -> Icons.Default.Cloud
             in 300..399 -> Icons.Default.Cloud
             in 400..409 -> Icons.Default.Cloud
             in 410..439 -> Icons.Default.Grain
             in 440..499 -> Icons.Default.Thunderstorm
             in 500..599 -> Icons.Default.WbCloudy
             in 600..699 -> Icons.Default.Dehaze
            else -> Icons.Default.WbSunny
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
            340 -> R.string.condition_340
            341 -> R.string.condition_341
            342 -> R.string.condition_342
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
