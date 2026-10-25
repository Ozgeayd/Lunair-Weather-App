package com.example.hava.ui.viewmodel

import com.example.hava.data.model.City
import com.example.hava.data.model.CityWithWeather

sealed interface CityUiState {
    object Idle : CityUiState
    object Loading : CityUiState
    data class Success(val cities: List<City>) : CityUiState
    object Empty : CityUiState
    data class Error(val message: String) : CityUiState
}

sealed interface DetailUiState {
    object None : DetailUiState
    data class PagerView(
        val citiesWithWeather: List<CityWithWeather>,
        val initialIndex: Int
    ) : DetailUiState
}