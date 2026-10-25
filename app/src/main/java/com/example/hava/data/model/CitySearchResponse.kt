package com.example.hava.data.model

import com.google.gson.annotations.SerializedName

data class CitySearchResponse(
    @SerializedName("results") val results: List<City>?
)