package com.example.hava.data.repository

import com.example.hava.data.model.City
import com.example.hava.data.model.CityWithWeather
import com.example.hava.data.model.WeatherResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class WeatherRepositoryImpl(
    private val legacyRepository: CityRepository = CityRepository()
) : WeatherRepository {

    override suspend fun searchCity(query: String): Result<List<City>> {
        return legacyRepository.searchCity(query)
    }

    override suspend fun getWeather(latitude: Double, longitude: Double): Result<WeatherResponse> {
        return legacyRepository.getWeather(latitude, longitude)
    }

    override suspend fun getFavoritesWithWeather(favorites: List<City>): List<CityWithWeather> = coroutineScope {
        favorites.map { city ->
            async {
                val result = legacyRepository.getWeather(city.latitude, city.longitude)
                CityWithWeather(
                    city = city,
                    weather = result.getOrNull(),
                    isLoading = false
                )
            }
        }.awaitAll()
    }
}