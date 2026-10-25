package com.example.hava.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hava.data.model.City
import com.example.hava.ui.components.CityCard
import com.example.hava.ui.components.CurrentLocationCard
import com.example.hava.ui.components.WeatherDetailSheet
import com.example.hava.ui.viewmodel.CityUiState
import com.example.hava.ui.viewmodel.CityViewModel
import com.example.hava.ui.viewmodel.DetailUiState
import kotlinx.coroutines.launch

private val SkyTopMutedBlue = Color(0xFF2E6CA6)
private val SkyCenterSoftBlue = Color(0xFF5A9FE2)
private val SkyCloudAura = Color(0xFFB5D7F8)
private val PureCloudLight = Color(0xFFF2F8FD)
private val SearchBarBg = Color(0xFF132034)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    viewModel: CityViewModel = viewModel()
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    val detailState by viewModel.detailState.collectAsState()
    val currentLocData by viewModel.currentLocationWeather.collectAsState()
    val favoriteCities by viewModel.favoriteCities.collectAsState()
    val focusManager = LocalFocusManager.current

    // Snackbar için durum ve Coroutine Scope
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            viewModel.fetchRealLocation()
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    // Pager ile Detay Ekranı Kontrolü
    when (val detail = detailState) {
        is DetailUiState.PagerView -> {
            WeatherDetailSheet(
                items = detail.citiesWithWeather,
                initialIndex = detail.initialIndex,
                isFavorite = { viewModel.isFavorite(it) },
                onToggleFavorite = { viewModel.toggleFavorite(it) },
                onBack = {
                    viewModel.closeDetail()
                    viewModel.loadFavorites()
                }
            )
            return
        }
        is DetailUiState.None -> {}
    }

    // Ana Ekran
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

        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = Color(0xFF1E2D42),
                        contentColor = Color.White,
                        actionColor = Color(0xFF70B6F6),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "🌙 Lunair",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 20.sp
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        if (it.isBlank()) viewModel.searchCity("")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Şehir adı arayın...", color = Color.White.copy(alpha = 0.6f)) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SearchBarBg,
                        unfocusedContainerColor = SearchBarBg,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedBorderColor = Color.White.copy(alpha = 0.4f),
                        unfocusedBorderColor = Color.Transparent
                    ),
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color.White.copy(alpha = 0.7f))
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = {
                                searchQuery = ""
                                viewModel.searchCity("")
                            }) {
                                Icon(Icons.Default.Clear, contentDescription = "Temizle", tint = Color.White)
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            focusManager.clearFocus()
                            viewModel.searchCity(searchQuery)
                        }
                    )
                )

                if (searchQuery.isBlank() || uiState is CityUiState.Idle) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        if (currentLocData != null) {
                            item {
                                CurrentLocationCard(
                                    data = currentLocData,
                                    onClick = {
                                        currentLocData?.let {
                                            viewModel.openDetailPager(it.city)
                                        }
                                    }
                                )
                            }
                        }

                        item {
                            Text(
                                text = "Favori Şehirler",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }

                        if (favoriteCities.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Henüz favori şehir eklemediniz.\nŞehir aratıp detayından kalbe basabilirsiniz.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        } else {
                            // Kaydırarak Silinebilir Favori Kartları
                            items(favoriteCities, key = { it.city.id }) { item ->
                                val dismissState = rememberSwipeToDismissBoxState(
                                    confirmValueChange = { dismissValue ->
                                        if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                                            viewModel.removeFavoriteWithUndo(item.city)
                                            scope.launch {
                                                val result = snackbarHostState.showSnackbar(
                                                    message = "${item.city.name} favorilerden kaldırıldı",
                                                    actionLabel = "Geri Al",
                                                    duration = SnackbarDuration.Short
                                                )
                                                if (result == SnackbarResult.ActionPerformed) {
                                                    viewModel.restoreRecentlyDeleted()
                                                }
                                            }
                                            true
                                        } else {
                                            false
                                        }
                                    }
                                )

                                SwipeToDismissBox(
                                    state = dismissState,
                                    enableDismissFromStartToEnd = false, // Sadece sağdan sola kaydırma aktif
                                    backgroundContent = {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(horizontal = 16.dp, vertical = 6.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(Color(0xFFE53935)), // Kırmızı çöp kutusu zemini
                                            contentAlignment = Alignment.CenterEnd
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Sil",
                                                tint = Color.White,
                                                modifier = Modifier.padding(end = 24.dp)
                                            )
                                        }
                                    }
                                ) {
                                    val tempText = item.weather?.currentWeather?.temperature?.let { "${it.toInt()}°" } ?: "--°"
                                    CityCard(
                                        city = item.city,
                                        temp = tempText,
                                        onClick = { viewModel.openDetailPager(item.city) }
                                    )
                                }
                            }
                        }
                    }
                } else {
                    when (val state = uiState) {
                        is CityUiState.Loading -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = Color.White)
                            }
                        }
                        is CityUiState.Empty -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(text = "Şehir bulunamadı.", style = MaterialTheme.typography.bodyLarge, color = Color.White)
                            }
                        }
                        is CityUiState.Error -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Button(onClick = { viewModel.retry() }) {
                                        Text("Tekrar Dene")
                                    }
                                }
                            }
                        }
                        is CityUiState.Success -> {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(state.cities, key = { it.id }) { city ->
                                    CityCard(
                                        city = city,
                                        onClick = { viewModel.openDetailPager(city) }
                                    )
                                }
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}