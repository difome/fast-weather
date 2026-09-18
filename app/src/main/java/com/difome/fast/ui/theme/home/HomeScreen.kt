package com.difome.fast.ui.theme.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.difome.fast.data.model.WeatherUI
import com.difome.fast.data.repository.loadWeather


@Composable
fun HomeScreen(name: String, modifier: Modifier = Modifier) {
    var count by remember { mutableIntStateOf(0) }

//
//    val weatherData = WeatherUI(
//        city = "Krasnodar,RU",
//        feelsLike = 12,
//        temp = 42,
//        minTemp = 12,
//        maxTemp = 42,
//    )
    var weatherData by remember { mutableStateOf<WeatherUI?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        try {
            weatherData = loadWeather()
        } catch (e: Exception) {
            error = e.message
        }
    }
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Привет, $name")

        Text("Count, $count")

        Text(weatherData?.city ?: "Загрузка...")
        Text("${weatherData?.temp ?: "--"}°C")

        Button(
            onClick = {
                count++
            }
        ) {
            Text("+1")
        }

        Button(
            onClick = {
                count--
            },
            enabled = count > 0
        ) {
            Text("-1")
        }
    }
}