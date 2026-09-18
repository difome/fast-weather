package com.difome.fast.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.difome.fast.R
import com.difome.fast.data.model.LocationSuggestion
import com.difome.fast.data.model.WeatherUI
import com.difome.fast.ui.home.components.CitySearchDialog
import com.difome.fast.ui.home.components.HourlyForecastRow
import com.difome.fast.ui.home.components.WeatherHeroCard
import com.difome.fast.ui.home.components.WeatherMetricsRow
import com.difome.fast.ui.home.components.WeatherSummaryCard
import com.difome.fast.ui.theme.MyFastTheme

@Composable
fun HomeScreen(
    name: String,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchSuggestions by viewModel.searchSuggestions.collectAsStateWithLifecycle()

    val currentLocale = LocalConfiguration.current.locales.get(0)

    LaunchedEffect(currentLocale) {
        viewModel.fetchWeather()
    }

    HomeScreenContent(
        name = name,
        uiState = uiState,
        searchSuggestions = searchSuggestions,
        onSearchQueryChange = { viewModel.searchCities(it) },
        onCitySelected = { viewModel.fetchWeather(it) },
        modifier = modifier
    )
}

@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    name: String,
    uiState: HomeUiState,
    searchSuggestions: List<LocationSuggestion> = emptyList(),
    onSearchQueryChange: (String) -> Unit = {},
    onCitySelected: (String) -> Unit = {}
) {
    var isSearchOpen by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val dismissSearch = {
        isSearchOpen = false
        searchQuery = ""
        onSearchQueryChange("")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.welcome_message, name),
            style = MaterialTheme.typography.headlineSmall
        )

        when (uiState) {
            is HomeUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is HomeUiState.Success -> {
                val weather = uiState.weather

                // 1. Главная синяя карточка погоды
                WeatherHeroCard(
                    weather = weather,
                    onCityClick = { isSearchOpen = true }
                )

                // 2. Карточки показателей (Влажность + Ветер)
                WeatherMetricsRow(weather = weather)

                // 3. Карточка текстового прогноза
                WeatherSummaryCard(summaryText = weather.verbalSummary)

                HourlyForecastRow(hourlyList = weather.hourlyForecast)            }

            is HomeUiState.Error -> {
                Text(
                    text = stringResource(id = R.string.error_loading),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    // 4. Окно поиска города
    CitySearchDialog(
        isOpen = isSearchOpen,
        searchQuery = searchQuery,
        onSearchQueryChange = {
            searchQuery = it
            onSearchQueryChange(it)
        },
        suggestions = searchSuggestions,
        onCitySelected = { cityId ->
            onCitySelected(cityId)
            dismissSearch()
        },
        onDismiss = dismissSearch
    )
}

@Preview(showBackground = true, name = "Weather Preview")
@Composable
fun HomeScreenSuccessPreview() {
    MyFastTheme {
        HomeScreenContent(
            name = "Fast",
            uiState = HomeUiState.Success(
                weather = WeatherUI(
                    city = "Austin, Texas",
                    temp = 32,
                    feelsLike = 34,
                    minTemp = 24,
                    maxTemp = 36,
                    conditionCode = 0,
                    humidity = 45,
                    windSpeed = 5.5,
                    verbalSummary = "Sunny and hot day in Austin, Texas. Great weather to stay hydrated!"
                )
            )
        )
    }
}
