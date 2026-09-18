package com.difome.fast.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.difome.fast.data.repository.loadWeather
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        fetchWeather()
    }

    fun fetchWeather() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val weather = loadWeather()
                _uiState.value = HomeUiState.Success(weather)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Err")
            }
        }
    }

}