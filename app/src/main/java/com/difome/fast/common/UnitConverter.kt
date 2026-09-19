package com.difome.fast.common

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
}
