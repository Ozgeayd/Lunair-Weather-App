package com.example.hava.data.repository

import com.example.hava.data.model.City
import com.example.hava.data.model.WeatherResponse
import com.example.hava.data.remote.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class CityRepository {
    // Şehir arama
    suspend fun searchCity(query: String): Result<List<City>> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.cityApi.searchCities(name = query)
            if (response.isSuccessful) {
                val list = response.body()?.results ?: emptyList()
                Result.success(list)
            } else {
                Result.failure(Exception("Sunucu hatası: ${response.code()}"))
            }
        } catch (e: IOException) {
            Result.failure(Exception("İnternet bağlantınızı kontrol edin."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Hava durumu detayını çekme
    suspend fun getWeather(lat: Double, lon: Double): Result<WeatherResponse> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClient.weatherApi.getForecast(latitude = lat, longitude = lon)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Hava durumu alınamadı: ${response.code()}"))
            }
        } catch (e: IOException) {
            Result.failure(Exception("İnternet bağlantınızı kontrol edin."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}