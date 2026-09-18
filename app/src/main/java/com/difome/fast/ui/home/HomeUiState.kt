package com.difome.fast.ui.home

import com.difome.fast.data.model.WeatherUI

sealed interface HomeUiState {
    object Loading: HomeUiState
    data class Success(val weather: WeatherUI) : HomeUiState
    data class Error(val message: String) : HomeUiState
}