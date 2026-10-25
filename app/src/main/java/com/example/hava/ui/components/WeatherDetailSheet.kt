package com.example.hava.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hava.data.model.City
import com.example.hava.data.model.CityWithWeather
import com.example.hava.ui.model.CityDetailUiModel
import com.example.hava.ui.model.toDetailUiModel

private val SkyTopMutedBlue = Color(0xFF2E6CA6)
private val SkyCenterSoftBlue = Color(0xFF5A9FE2)
private val SkyCloudAura = Color(0xFFB5D7F8)
private val PureCloudLight = Color(0xFFF2F8FD)

private val CardSolidBg = Color(0xFF132034)
private val SelectedHourBg = Color(0xFF223B60)
private val TextSecondary = Color(0xFF90A3BF)
private val TempBarBg = Color(0xFF223550)
private val TempBarActive = Color(0xFFF5A623)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherDetailSheet(
    items: List<CityWithWeather>,
    initialIndex: Int,
    isFavorite: (Long) -> Boolean,
    onToggleFavorite: (City) -> Unit,
    onBack: () -> Unit
) {
    val pagerState = rememberPagerState(
        initialPage = initialIndex.coerceIn(0, (items.size - 1).coerceAtLeast(0)),
        pageCount = { items.size }
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(SkyTopMutedBlue, SkyCenterSoftBlue, SkyCloudAura)
                ),
                size = size
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(PureCloudLight.copy(alpha = 0.55f), Color.Transparent),
                    center = Offset(width * 0.85f, height * 0.16f),
                    radius = width * 0.80f
                ),
                center = Offset(width * 0.85f, height * 0.16f),
                radius = width * 0.80f
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(PureCloudLight.copy(alpha = 0.45f), Color.Transparent),
                    center = Offset(width * 0.15f, height * 0.30f),
                    radius = width * 0.85f
                ),
                center = Offset(width * 0.15f, height * 0.30f),
                radius = width * 0.85f
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.10f), Color(0xFF0F1B2C).copy(alpha = 0.50f))
                    )
                )
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val item = items.getOrNull(page)
            if (item != null) {
                val uiModel = remember(item) { item.toDetailUiModel() }
                SingleCityDetailView(
                    model = uiModel,
                    isFav = isFavorite(item.city.id),
                    onToggleFavorite = { onToggleFavorite(item.city) },
                    onBack = onBack
                )
            }
        }

        if (items.size > 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp)
                    .background(Color.Black.copy(alpha = 0.25f), shape = RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(items.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 8.dp else 6.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) Color.White else Color.White.copy(alpha = 0.4f))
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SingleCityDetailView(
    model: CityDetailUiModel,
    isFav: Boolean,
    onToggleFavorite: () -> Unit,
    onBack: () -> Unit
) {
    var isFavState by remember(isFav) { mutableStateOf(isFav) }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "🌙 Lunair", // veya ☁️ Lunair
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = "📍 ${model.city.name}",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        IconButton(onClick = {
                            isFavState = !isFavState
                            onToggleFavorite()
                        }) {
                            Icon(
                                imageVector = if (isFavState) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Favori",
                                tint = if (isFavState) Color(0xFFFF4757) else Color.White
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        if (model.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 36.dp)
            ) {
                item {
                    Text(
                        text = model.dateText,
                        color = Color.White.copy(alpha = 0.90f),
                        fontSize = 14.sp
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = model.currentTemp,
                                fontSize = 68.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.White
                            )
                            Text(
                                text = model.statusText,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = model.apparentTemp,
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                            Text(
                                text = "En yüksek ${model.maxTemp}  |  En düşük ${model.minTemp}",
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }

                        Text(
                            text = model.statusEmoji,
                            fontSize = 80.sp
                        )
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardSolidBg),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            MetricItem(icon = "💨", label = "Rüzgâr", value = model.windText, sub = "KD")
                            MetricItem(icon = "💧", label = "Nem", value = model.humidityText, sub = null)
                            MetricItem(icon = "🌧️", label = "Yağış", value = model.precipitationText, sub = null)
                            MetricItem(icon = "☀️", label = "UV İndeksi", value = model.uvText, sub = "Orta")
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardSolidBg),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Saatlik Tahmin",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "24 saatlik görünüm →",
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(model.hourlyItems) { item ->
                                    Column(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (item.isCurrent) SelectedHourBg else Color.Transparent)
                                            .padding(horizontal = 14.dp, vertical = 10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = item.hourText,
                                            color = if (item.isCurrent) Color.White else TextSecondary,
                                            fontSize = 12.sp
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(text = item.emoji, fontSize = 20.sp)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = item.tempText,
                                            color = Color.White,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardSolidBg),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "7 Günlük Tahmin",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "Detaylı görünüm →",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            model.dailyItems.forEach { daily ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = daily.emoji, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = daily.dayName,
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        modifier = Modifier.width(80.dp),
                                        fontWeight = if (daily.isToday) FontWeight.Bold else FontWeight.Normal
                                    )
                                    Text(
                                        text = daily.description,
                                        color = TextSecondary,
                                        fontSize = 12.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = daily.minTemp,
                                        color = TextSecondary,
                                        fontSize = 13.sp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .width(70.dp)
                                            .height(4.dp)
                                            .clip(CircleShape)
                                            .background(TempBarBg)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .fillMaxWidth(0.6f)
                                                .align(Alignment.Center)
                                                .clip(CircleShape)
                                                .background(TempBarActive)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = daily.maxTemp,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    val advice = model.advice
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = CardSolidBg),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFFF5A623).copy(alpha = 0.2f),
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Lightbulb,
                                                contentDescription = null,
                                                tint = Color(0xFFF5A623),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Günün Önerisi",
                                            color = Color.White.copy(alpha = 0.6f),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = advice.title,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = when {
                                        advice.outdoorScore >= 8 -> Color(0xFF2ED573).copy(alpha = 0.2f)
                                        advice.outdoorScore >= 5 -> Color(0xFFFFA502).copy(alpha = 0.2f)
                                        else -> Color(0xFFFF4757).copy(alpha = 0.2f)
                                    }
                                ) {
                                    Text(
                                        text = "Skor: ${advice.outdoorScore}/10",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        color = when {
                                            advice.outdoorScore >= 8 -> Color(0xFF2ED573)
                                            advice.outdoorScore >= 5 -> Color(0xFFFFA502)
                                            else -> Color(0xFFFF4757)
                                        },
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = advice.description,
                                color = TextSecondary,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )

                            if (advice.itemsToTake.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = "Yanına Al & Kıyafet:",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    advice.itemsToTake.forEach { itemText ->
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = Color(0xFF1E2F46)
                                        ) {
                                            Text(
                                                text = itemText,
                                                color = Color.White,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricItem(icon: String, label: String, value: String, sub: String?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = icon, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, color = TextSecondary, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        if (sub != null) {
            Text(text = sub, color = TextSecondary, fontSize = 10.sp)
        }
    }
}