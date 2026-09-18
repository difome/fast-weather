package com.difome.fast.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.difome.fast.R
import com.difome.fast.data.model.LocationSuggestion
import com.difome.fast.data.model.WeatherConditionHelper
import com.difome.fast.data.model.WeatherUI
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

@OptIn(ExperimentalMaterial3Api::class)
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

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { isSearchOpen = true }
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = weather.city,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${weather.temp}°C",
                                style = MaterialTheme.typography.displayLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )

                            Spacer(modifier = Modifier.width(24.dp))

                            Icon(
                                imageVector = WeatherConditionHelper.getIcon(weather.conditionCode),
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = Color(0xFFFFB703)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = stringResource(id = WeatherConditionHelper.getTitleRes(weather.conditionCode)),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(id = R.string.feels_like, weather.feelsLike),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "${weather.minTemp}°/${weather.maxTemp}°",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedCard(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = stringResource(id = R.string.humidity),
                                style = MaterialTheme.typography.labelMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${weather.humidity}%",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }

                    // Карточка 2 (Ветер)
                    OutlinedCard(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = stringResource(id = R.string.wind),
                                style = MaterialTheme.typography.labelMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(
                                    id = R.string.wind_speed_value,
                                    weather.windSpeed
                                ), style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }
                }

                if (weather.verbalSummary.isNotEmpty()) {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.forecast_title),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = weather.verbalSummary,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            is HomeUiState.Error -> {
                Text(
                    text = stringResource(id = R.string.error_loading),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    if (isSearchOpen) {
        SearchBar(
            query = searchQuery,
            onQueryChange = {
                searchQuery = it
                onSearchQueryChange(it)
            },
            onSearch = { isSearchOpen = false },
            active = true,
            onActiveChange = { isSearchOpen = it },
            placeholder = { Text(text = stringResource(id = R.string.search_city_placeholder)) },
            leadingIcon = {
                IconButton(onClick = { isSearchOpen = false }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = {
                        searchQuery = ""
                        onSearchQueryChange("")
                    }) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                items(searchSuggestions, key = { it.id }) { location ->
                    Surface(
                        onClick = {
                            onCitySelected(location.id)
                            isSearchOpen = false
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = location.title,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (location.description.isNotEmpty()) {
                                Text(
                                    text = location.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchCitySheetContent(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    suggestions: List<LocationSuggestion>,
    onCitySelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.search_city_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onQueryChange,
            placeholder = { Text(text = stringResource(id = R.string.search_city_placeholder)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(suggestions, key = { it.id }) { location ->
                Surface(
                    onClick = { onCitySelected(location.id) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = location.title,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (location.description.isNotEmpty()) {
                            Text(
                                text = location.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Search City Preview")
@Composable
fun SearchCitySheetPreview() {
    MyFastTheme {
        SearchCitySheetContent(
            searchQuery = "Rusan",
            onQueryChange = {},
            suggestions = listOf(
                LocationSuggestion(
                    id = "rusaniv",
                    title = "Rusaniv",
                    description = "Kyiv region, Brovary district"
                ),
                LocationSuggestion(
                    id = "rusanivka",
                    title = "Rusanivka",
                    description = "Sumy region, Romny district"
                )
            ),
            onCitySelected = {}
        )
    }
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
