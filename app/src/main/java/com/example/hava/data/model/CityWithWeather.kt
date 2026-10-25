package com.example.hava.data.model

data class CityWithWeather(
    val city: City,
    val weather: WeatherResponse? = null,
    val isLoading: Boolean = false
)