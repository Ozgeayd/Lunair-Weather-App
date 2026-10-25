package com.example.hava.data.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("current_weather") val currentWeather: CurrentWeather?,
    @SerializedName("hourly") val hourly: HourlyWeather?,
    @SerializedName("daily") val daily: DailyWeather?
)

data class CurrentWeather(
    @SerializedName("temperature") val temperature: Double,
    @SerializedName("windspeed") val windSpeed: Double,
    @SerializedName("weathercode") val weatherCode: Int,
    @SerializedName("time") val time: String
)

data class HourlyWeather(
    @SerializedName("time") val time: List<String>,
    @SerializedName("temperature_2m") val temperatures: List<Double>,
    @SerializedName("weathercode") val weatherCodes: List<Int>,
    @SerializedName("relative_humidity_2m") val humidities: List<Int>?,
    @SerializedName("precipitation_probability") val precipitationProbabilities: List<Int>?,
    @SerializedName("uv_index") val uvIndexes: List<Double>?,
    @SerializedName("apparent_temperature") val apparentTemperatures: List<Double>?
)

data class DailyWeather(
    @SerializedName("time") val time: List<String>,
    @SerializedName("temperature_2m_max") val maxTemp: List<Double>,
    @SerializedName("temperature_2m_min") val minTemp: List<Double>,
    @SerializedName("weathercode") val weatherCode: List<Int>
)