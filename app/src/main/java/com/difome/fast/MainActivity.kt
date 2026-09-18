package com.difome.fast

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.difome.fast.ui.theme.MyFastTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL


data class WeatherUI(
    val city: String,
    val temp: Int,
    val feelsLike: Int,
    val minTemp: Int,
    val maxTemp: Int
)

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var navController = rememberNavController()
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination?.route

            MyFastTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(

                            title = {
                                if (currentRoute  == "settings") {
                                    Text("Настройки")
                                } else {
                                    Text("My Jopa")
                                }
                            },
                            actions = {
                                TextButton(
                                    onClick = {
                                        navController.navigate("settings")
                                    }
                                ) {
                                    Text("Settings")
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") {
                            HomeScreen(name = "Fast")
                        }
                        composable("settings") {
                            SettingsScreen(navController)
                        }
                    }
                }
            }
        }
    }
}

suspend fun loadWeather(): WeatherUI = withContext(Dispatchers.IO) {
    val connection = URL(
        "https://sinoptik.ua/api/weather/location/forecast/by_id"
    ).openConnection() as HttpURLConnection

    connection.requestMethod = "POST"
    connection.doOutput = true
    connection.setRequestProperty("Content-Type", "application/json")
    connection.setRequestProperty("Accept", "application/json")

    val body = """
        {
            "lang": "ukr",
            "location_id": "krasnodar",
            "forecast_days": 10
        }
    """.trimIndent()

    connection.outputStream.use {
        it.write(body.toByteArray())
    }

    if (connection.responseCode !in 200..299) {
        throw Exception("HTTP ${connection.responseCode}")
    }

    val response = connection.inputStream
        .bufferedReader()
        .use { it.readText() }

    val json = JSONObject(response)

    val city = json
        .getJSONObject("location")
        .getString("title")

    val forecast = json.getJSONObject("forecast")
    val date = forecast.keys().next()
    val today = forecast.getJSONObject(date)

    val now = today.getJSONObject("now")
    val temp = today.getJSONObject("temp")

    WeatherUI(
        city = city,
        temp = now.getInt("temp"),
        feelsLike = now.getInt("temp_feels"),
        minTemp = temp.getInt("min"),
        maxTemp = temp.getInt("max")
    )
}

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

@Composable
fun SettingsScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

    Button(
        onClick = {
            navController.popBackStack()
        }
    )
    {
        Text("Назад")
    }
}
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyFastTheme {
        HomeScreen("Android")
    }
}