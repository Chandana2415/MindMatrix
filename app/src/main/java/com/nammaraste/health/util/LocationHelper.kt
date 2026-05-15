package com.nammaraste.health.util

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.tasks.await

object LocationHelper {
    @SuppressLint("MissingPermission")
    suspend fun getLastLocation(context: Context): LatLng? {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        return try {
            val location = fusedLocationClient.lastLocation.await()
            location?.let { LatLng(it.latitude, it.longitude) }
        } catch (e: Exception) {
            null
        }
    }
}
