package com.difome.fast.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.difome.fast.data.model.LocationSuggestion
import com.difome.fast.data.repository.loadWeather
import com.difome.fast.data.repository.searchLocations
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private var currentLocationId: String = "krasnodar"

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _searchSuggestions = MutableStateFlow<List<LocationSuggestion>>(emptyList())
    val searchSuggestions: StateFlow<List<LocationSuggestion>> = _searchSuggestions.asStateFlow()

    init {
        fetchWeather("krasnodar")
    }

    fun fetchWeather(locationId: String = currentLocationId) {
        currentLocationId = locationId
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val weather = loadWeather(currentLocationId)
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
