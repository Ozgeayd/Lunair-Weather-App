package com.example.hava.data.local

import android.content.Context
import com.example.hava.data.model.City
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FavoritesManager(context: Context) {
    private val prefs = context.getSharedPreferences("weather_favorites", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getFavorites(): List<City> {
        val json = prefs.getString("fav_cities", null) ?: return emptyList()
        val type = object : TypeToken<List<City>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun toggleFavorite(city: City): Boolean {
        val currentList = getFavorites().toMutableList()
        val exists = currentList.any { it.id == city.id }
        if (exists) {
            currentList.removeAll { it.id == city.id }
        } else {
            currentList.add(city)
        }
        val json = gson.toJson(currentList)
        prefs.edit().putString("fav_cities", json).apply()
        return !exists // true ise eklendi, false ise çıkarıldı
    }

    fun isFavorite(cityId: Long): Boolean {
        return getFavorites().any { it.id == cityId }
    }
}