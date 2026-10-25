package com.example.hava.ui.model

import com.example.hava.data.model.City
import com.example.hava.data.model.CityWithWeather
import com.example.hava.ui.DayAdvice
import com.example.hava.ui.extractHour
import com.example.hava.ui.formatDayName
import com.example.hava.ui.formatDetailDate
import com.example.hava.ui.findCurrentHourIndex
import com.example.hava.ui.generateDayAdvice
import com.example.hava.ui.parseWeatherCode
import kotlin.math.roundToInt

data class HourlyUiItem(
    val hourText: String,
    val emoji: String,
    val tempText: String,
    val isCurrent: Boolean
)

data class DailyUiItem(
    val dayName: String,
    val description: String,
    val emoji: String,
    val minTemp: String,
    val maxTemp: String,
    val isToday: Boolean
)

data class CityDetailUiModel(
    val city: City,
    val dateText: String,
    val currentTemp: String,
    val statusText: String,
    val statusEmoji: String,
    val apparentTemp: String,
    val maxTemp: String,
    val minTemp: String,
    val windText: String,
    val humidityText: String,
    val precipitationText: String,
    val uvText: String,
    val hourlyItems: List<HourlyUiItem>,
    val dailyItems: List<DailyUiItem>,
    val advice: DayAdvice,
    val isLoading: Boolean
)

// UI ekranı hesaplama yapmasın, veriyi hazır paket alsın diye mapper fonksiyonu:
fun CityWithWeather.toDetailUiModel(): CityDetailUiModel {
    val current = weather?.currentWeather
    val weatherCode = current?.weatherCode ?: 0
    val (status, emoji) = parseWeatherCode(weatherCode)

    val hourIndex = findCurrentHourIndex(weather?.hourly?.time)
    val curTempInt = current?.temperature?.roundToInt() ?: 20
    val maxToday = weather?.daily?.maxTemp?.firstOrNull()?.roundToInt() ?: 22
    val minToday = weather?.daily?.minTemp?.firstOrNull()?.roundToInt() ?: 8

    val apparent = weather?.hourly?.apparentTemperatures?.getOrNull(hourIndex)?.roundToInt() ?: curTempInt
    val humidity = weather?.hourly?.humidities?.getOrNull(hourIndex) ?: 60
    val precip = weather?.hourly?.precipitationProbabilities?.getOrNull(hourIndex) ?: 0
    val uv = weather?.hourly?.uvIndexes?.getOrNull(hourIndex)?.roundToInt() ?: 0
    val wind = current?.windSpeed?.roundToInt() ?: 7

    val totalHours = weather?.hourly?.time?.size ?: 0
    val hourlyTimes = if (totalHours > hourIndex) {
        weather?.hourly?.time?.drop(hourIndex)?.take(12) ?: emptyList()
    } else emptyList()

    val hourlyList = hourlyTimes.mapIndexed { idx, timeStr ->
        val actualIdx = hourIndex + idx
        val isNow = idx == 0
        val code = weather?.hourly?.weatherCodes?.getOrNull(actualIdx) ?: 0
        val (_, hEmoji) = parseWeatherCode(code)
        val t = if (isNow) curTempInt else weather?.hourly?.temperatures?.getOrNull(actualIdx)?.roundToInt() ?: curTempInt

        HourlyUiItem(
            hourText = if (isNow) "Şimdi" else extractHour(timeStr),
            emoji = hEmoji,
            tempText = "$t°",
            isCurrent = isNow
        )
    }

    val dailyTimes = weather?.daily?.time ?: emptyList()
    val dailyList = dailyTimes.mapIndexed { idx, dStr ->
        val code = weather?.daily?.weatherCode?.getOrNull(idx) ?: 0
        val (desc, dEmoji) = parseWeatherCode(code)
        val minT = weather?.daily?.minTemp?.getOrNull(idx)?.roundToInt() ?: 8
        val maxT = weather?.daily?.maxTemp?.getOrNull(idx)?.roundToInt() ?: 22

        DailyUiItem(
            dayName = formatDayName(dStr, idx),
            description = desc,
            emoji = dEmoji,
            minTemp = "$minT°",
            maxTemp = "$maxT°",
            isToday = idx == 0
        )
    }

    val dayAdvice = generateDayAdvice(weatherCode, curTempInt, wind, precip, uv)

    return CityDetailUiModel(
        city = city,
        dateText = formatDetailDate(),
        currentTemp = "$curTempInt°",
        statusText = status,
        statusEmoji = emoji,
        apparentTemp = "Hissedilen $apparent°",
        maxTemp = "$maxToday°",
        minTemp = "$minToday°",
        windText = "$wind km/sa",
        humidityText = "%$humidity",
        precipitationText = "%$precip",
        uvText = "$uv",
        hourlyItems = hourlyList,
        dailyItems = dailyList,
        advice = dayAdvice,
        isLoading = isLoading && weather == null
    )
}