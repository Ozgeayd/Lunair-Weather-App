package com.example.hava.data.repository

import com.example.hava.data.model.City
import com.example.hava.data.model.CityWithWeather
import com.example.hava.data.model.WeatherResponse

interface WeatherRepository {
    suspend fun searchCity(query: String): Result<List<City>>
    suspend fun getWeather(latitude: Double, longitude: Double): Result<WeatherResponse>
    suspend fun getFavoritesWithWeather(favorites: List<City>): List<CityWithWeather>
}