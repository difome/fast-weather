package com.difome.fast.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.difome.fast.common.AppConstants
import com.difome.fast.data.local.CityPreferences
import com.difome.fast.data.local.SettingsPreferences
import com.difome.fast.data.model.LocationSuggestion
import com.difome.fast.data.repository.getSinoptikLanguage
import com.difome.fast.data.repository.loadWeather
import com.difome.fast.data.repository.searchLocations
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val cityPreferences = CityPreferences(application)
    private val settingsPreferences = SettingsPreferences(application)
    private var currentLocationId: String = AppConstants.defaultCityId
    private var lastFetchedLang: String = ""

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _searchSuggestions = MutableStateFlow<List<LocationSuggestion>>(emptyList())
    val searchSuggestions: StateFlow<List<LocationSuggestion>> = _searchSuggestions.asStateFlow()

    val isFahrenheit: StateFlow<Boolean> = settingsPreferences.isFahrenheit
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val windUnit: StateFlow<String> = settingsPreferences.windUnit
        .stateIn(viewModelScope, SharingStarted.Eagerly, "ms")

    init {
        viewModelScope.launch {
            val savedCityId = cityPreferences.selectedCityId.first()
            fetchWeather(locationId = savedCityId, forceReload = true)
        }
    }

    fun clearSearch() {
        _searchSuggestions.value = emptyList()
    }

    fun fetchWeather(locationId: String = currentLocationId, forceReload: Boolean = false) {
        val currentLang = getSinoptikLanguage()
        val isSameCity = currentLocationId == locationId
        val isSameLang = lastFetchedLang == currentLang
        val hasData = _uiState.value is HomeUiState.Success

        if (!forceReload && isSameCity && isSameLang && hasData) {
            return
        }

        currentLocationId = locationId
        lastFetchedLang = currentLang
        _searchSuggestions.value = emptyList()

        viewModelScope.launch {
            cityPreferences.saveSelectedCity(locationId)
            _uiState.value = HomeUiState.Loading
            try {
                val startTime = System.currentTimeMillis()
                val weather = loadWeather(currentLocationId)
                val elapsedTime = System.currentTimeMillis() - startTime
                if (elapsedTime < 500) {
                    delay(500 - elapsedTime)
                }
                _uiState.value = HomeUiState.Success(weather)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Err")
            }
        }
    }

    fun searchCities(query: String) {
        if (query.trim().length < 2) {
            _searchSuggestions.value = emptyList()
            return
        }
        viewModelScope.launch {
            try {
                val results = searchLocations(query)
                _searchSuggestions.value = results
            } catch (e: Exception) {
                _searchSuggestions.value = emptyList()
            }
        }
    }
}
