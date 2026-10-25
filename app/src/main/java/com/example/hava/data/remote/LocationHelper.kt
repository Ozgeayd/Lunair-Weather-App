package com.example.hava.data.remote

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import com.example.hava.data.model.City
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume

class LocationHelper(private val context: Context) {
    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocationCity(): City? = suspendCancellableCoroutine { continuation ->
        val cts = CancellationTokenSource()

        fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val city = resolveCityName(location.latitude, location.longitude)
                    continuation.resume(city)
                } else {
                    continuation.resume(null)
                }
            }
            .addOnFailureListener {
                continuation.resume(null)
            }

        continuation.invokeOnCancellation {
            cts.cancel()
        }
    }

    private fun resolveCityName(lat: Double, lon: Double): City {
        var cityName = "Mevcut Konum"
        var regionName = ""
        var country = "TR"

        try {
            val geocoder = Geocoder(context, Locale("tr"))
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(lat, lon, 1)
            if (!addresses.isNullOrEmpty()) {
                val addr = addresses[0]
                cityName = addr.adminArea ?: addr.subAdminArea ?: addr.locality ?: "Mevcut Konum"
                regionName = addr.subAdminArea ?: ""
                country = addr.countryCode ?: "TR"
            }
        } catch (_: Exception) {}

        return City(
            id = (lat * 1000 + lon * 1000).toLong(),
            name = cityName,
            countryCode = country,
            region = regionName,
            latitude = lat,
            longitude = lon
        )
    }
}