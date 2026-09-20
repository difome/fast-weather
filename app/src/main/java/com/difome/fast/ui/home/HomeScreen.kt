package com.difome.fast.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import com.difome.fast.common.AppConstants
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.difome.fast.R
import com.difome.fast.data.model.LocationSuggestion
import com.difome.fast.data.model.WeatherUI
import com.difome.fast.ui.home.components.CitySearchDialog
import com.difome.fast.ui.home.components.DailyForecastCard
import com.difome.fast.ui.home.components.HourlyForecastRow
import com.difome.fast.ui.home.components.WeatherHeroCard
import com.difome.fast.ui.home.components.WeatherMetricsRow
import com.difome.fast.ui.home.components.WeatherSummaryCard
import com.difome.fast.ui.theme.MyFastTheme

@Composable
fun HomeScreen(
    name: String,
    modifier: Modifier = Modifier,
    onSettingsClick: () -> Unit = {},
    onDailyForecastClick: () -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchSuggestions by viewModel.searchSuggestions.collectAsStateWithLifecycle()
    val isFahrenheit by viewModel.isFahrenheit.collectAsStateWithLifecycle()
    val windUnit by viewModel.windUnit.collectAsStateWithLifecycle()
    val pressureUnit by viewModel.pressureUnit.collectAsStateWithLifecycle()

    HomeScreenContent(
        name = name,
        uiState = uiState,
        isFahrenheit = isFahrenheit,
        windUnit = windUnit,
        pressureUnit = pressureUnit,
        searchSuggestions = searchSuggestions,
        onSearchQueryChange = { viewModel.searchCities(it) },
        onCitySelected = { viewModel.fetchWeather(it, forceReload = true) },
        onSettingsClick = onSettingsClick,
        onDailyForecastClick = onDailyForecastClick,
        onRefresh = { viewModel.fetchWeather(forceReload = true) },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    name: String,
    uiState: HomeUiState,
    isFahrenheit: Boolean = false,
    windUnit: String = AppConstants.WIND_UNIT_MS,
    pressureUnit: String = AppConstants.PRESSURE_UNIT_MBAR,
    searchSuggestions: List<LocationSuggestion> = emptyList(),
    onSearchQueryChange: (String) -> Unit = {},
    onCitySelected: (String) -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onDailyForecastClick: () -> Unit = {},
    onRefresh: () -> Unit = {}
) {
    var isSearchOpen by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    var isUserRefreshing by remember { mutableStateOf(false) }
    var lastSuccessWeather by remember { mutableStateOf<WeatherUI?>(null) }
    val updatedMessage = stringResource(id = R.string.weather_updated)

    if (uiState is HomeUiState.Success) {
        lastSuccessWeather = uiState.weather
    }

    val displayWeather = (uiState as? HomeUiState.Success)?.weather ?: lastSuccessWeather

    val dismissSearch = {
        isSearchOpen = false
        searchQuery = ""
        onSearchQueryChange("")
    }
    val isRefreshing = isUserRefreshing && uiState is HomeUiState.Loading

    LaunchedEffect(uiState) {
        if (isUserRefreshing && uiState !is HomeUiState.Loading) {
            isUserRefreshing = false
            if (uiState is HomeUiState.Success) {
                snackbarHostState.showSnackbar(updatedMessage)
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isUserRefreshing = true
                onRefresh()
            },
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.welcome_message, name),
                        style = MaterialTheme.typography.headlineSmall
                    )

                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(id = R.string.settings_title),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                val weather = displayWeather
                if (weather != null) {
                    WeatherHeroCard(
                        weather = weather,
                        isFahrenheit = isFahrenheit,
                        onCityClick = { isSearchOpen = true }
                    )

                    HourlyForecastRow(
                        hourlyList = weather.hourlyForecast,
                        isFahrenheit = isFahrenheit,
                        currentCityHour = weather.currentCityHour
                    )

                    DailyForecastCard(
                        dailyList = weather.dailyForecast,
                        isFahrenheit = isFahrenheit,
                        onMoreClick = onDailyForecastClick
                    )

                    WeatherMetricsRow(
                        weather = weather,
                        windUnit = windUnit,
                        pressureUnit = pressureUnit
                    )

                    WeatherSummaryCard(summaryText = weather.verbalSummary)
                } else if (uiState is HomeUiState.Loading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                } else if (uiState is HomeUiState.Error) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(id = R.string.error_loading),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        if (uiState.message.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = uiState.message,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }

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
