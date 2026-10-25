package com.example.hava.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.hava.data.local.FavoritesManager
import com.example.hava.data.model.City
import com.example.hava.data.model.CityWithWeather
import com.example.hava.data.remote.LocationHelper
import com.example.hava.data.repository.WeatherRepository
import com.example.hava.data.repository.WeatherRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Constructor sadece Application alacak (Android çökmesini engellemek için)
class CityViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WeatherRepository = WeatherRepositoryImpl()
    private val favManager = FavoritesManager(application)
    private val locationHelper = LocationHelper(application)

    private val _uiState = MutableStateFlow<CityUiState>(CityUiState.Idle)
    val uiState: StateFlow<CityUiState> = _uiState.asStateFlow()

    private val _detailState = MutableStateFlow<DetailUiState>(DetailUiState.None)
    val detailState: StateFlow<DetailUiState> = _detailState.asStateFlow()

    private val _currentLocationWeather = MutableStateFlow<CityWithWeather?>(null)
    val currentLocationWeather: StateFlow<CityWithWeather?> = _currentLocationWeather.asStateFlow()

    private val _favoriteCities = MutableStateFlow<List<CityWithWeather>>(emptyList())
    val favoriteCities: StateFlow<List<CityWithWeather>> = _favoriteCities.asStateFlow()

    private var recentlyDeletedCity: City? = null
    private var lastQuery: String = ""

    init {
        loadFavorites()
    }

    init {
        loadFavorites()
    }

    fun fetchRealLocation() {
        _currentLocationWeather.value = CityWithWeather(
            city = City(0, "Konum Aranıyor...", "TR", "", 0.0, 0.0),
            isLoading = true
        )

        viewModelScope.launch {
            val city = locationHelper.getCurrentLocationCity()
            if (city != null) {
                val weatherRes = repository.getWeather(city.latitude, city.longitude)
                _currentLocationWeather.value = CityWithWeather(
                    city = city,
                    weather = weatherRes.getOrNull(),
                    isLoading = false
                )
            } else {
                _currentLocationWeather.value = null
            }
        }
    }

    fun loadFavorites() {
        val saved = favManager.getFavorites()
        if (saved.isEmpty()) {
            _favoriteCities.value = emptyList()
            return
        }

        viewModelScope.launch {
            val loadedFavorites = repository.getFavoritesWithWeather(saved)
            _favoriteCities.value = loadedFavorites
        }
    }

    fun toggleFavorite(city: City) {
        favManager.toggleFavorite(city)
        loadFavorites()
    }

    fun isFavorite(cityId: Long): Boolean = favManager.isFavorite(cityId)

    fun removeFavoriteWithUndo(city: City) {
        recentlyDeletedCity = city
        favManager.toggleFavorite(city)
        loadFavorites()
    }

    fun restoreRecentlyDeleted() {
        recentlyDeletedCity?.let { city ->
            favManager.toggleFavorite(city)
            loadFavorites()
            recentlyDeletedCity = null
        }
    }

    fun openDetailPager(clickedCity: City) {
        val fullList = mutableListOf<CityWithWeather>()
        _currentLocationWeather.value?.let { fullList.add(it) }
        fullList.addAll(_favoriteCities.value)

        var targetIndex = fullList.indexOfFirst { it.city.id == clickedCity.id }
        if (targetIndex == -1) {
            val newItem = CityWithWeather(city = clickedCity, isLoading = true)
            fullList.add(newItem)
            targetIndex = fullList.lastIndex

            viewModelScope.launch {
                val res = repository.getWeather(clickedCity.latitude, clickedCity.longitude)
                val updated = _detailState.value
                if (updated is DetailUiState.PagerView) {
                    val currentList = updated.citiesWithWeather.toMutableList()
                    val idx = currentList.indexOfFirst { it.city.id == clickedCity.id }
                    if (idx != -1) {
                        currentList[idx] = CityWithWeather(city = clickedCity, weather = res.getOrNull(), isLoading = false)
                        _detailState.value = DetailUiState.PagerView(currentList, updated.initialIndex)
                    }
                }
            }
        }

        _detailState.value = DetailUiState.PagerView(fullList, targetIndex)
    }

    fun closeDetail() {
        _detailState.value = DetailUiState.None
    }

    fun searchCity(query: String) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) {
            _uiState.value = CityUiState.Idle
            return
        }

        lastQuery = trimmed
        _uiState.value = CityUiState.Loading

        viewModelScope.launch {
            val result = repository.searchCity(trimmed)
            result.fold(
                onSuccess = { list ->
                    _uiState.value = if (list.isEmpty()) CityUiState.Empty else CityUiState.Success(list)
                },
                onFailure = { error ->
                    _uiState.value = CityUiState.Error(error.localizedMessage ?: "Bilinmeyen hata")
                }
            )
        }
    }

    fun retry() {
        if (lastQuery.isNotEmpty()) searchCity(lastQuery)
    }
}