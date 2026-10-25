package com.example.hava.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hava.data.model.CityWithWeather
import com.example.hava.ui.parseWeatherCode
import kotlin.math.roundToInt

// Mevcut konuma özel daha canlı ve derin gece mavisi degrade tonları
private val LiveCardTop = Color(0xFF1E3A5F)
private val LiveCardBottom = Color(0xFF10223A)
private val LiveBorderColor = Color(0xFF4A80B0).copy(alpha = 0.40f)
private val TextSecondary = Color(0xFFB0C4DE)
private val DividerColor = Color(0xFFFFFFFF).copy(alpha = 0.12f)

@Composable
fun CurrentLocationCard(
    data: CityWithWeather?,
    onClick: () -> Unit
) {
    if (data == null) return

    val current = data.weather?.currentWeather
    val weatherCode = current?.weatherCode ?: 0
    val (statusText, statusEmoji) = parseWeatherCode(weatherCode)
    val temp = current?.temperature?.roundToInt()
    val wind = current?.windSpeed?.roundToInt() ?: 0
    val apparentTemp = data.weather?.hourly?.apparentTemperatures?.firstOrNull()?.roundToInt() ?: temp
    val humidity = data.weather?.hourly?.humidities?.firstOrNull() ?: 60

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp, LiveBorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(LiveCardTop, LiveCardBottom)
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                // Üst Kısım: Konum Başlığı ve Anlık Derece
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "📍", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Canlı Konum",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFF70B6F6),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = data.city.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }

                    if (data.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            color = Color.White,
                            strokeWidth = 3.dp
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = statusEmoji, fontSize = 48.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "${temp ?: "--"}°",
                                fontSize = 46.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }
                }

                // Alt Kısım: Kartı uzatan detaylı mikro metrikler
                if (!data.isLoading && data.weather != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = DividerColor, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        QuickMetric(label = "Hissedilen", value = "${apparentTemp ?: "--"}°")
                        QuickMetric(label = "Rüzgâr", value = "$wind km/sa")
                        QuickMetric(label = "Nem", value = "%$humidity")
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickMetric(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}