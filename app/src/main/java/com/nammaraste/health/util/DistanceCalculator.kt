package com.nammaraste.health.util

import com.google.android.gms.maps.model.LatLng
import kotlin.math.*

object DistanceCalculator {
    fun haversineDistanceKm(a: LatLng, b: LatLng): Double {
        val r = 6371.0 // Earth's radius in kilometers
        val dLat = Math.toRadians(b.latitude - a.latitude)
        val dLng = Math.toRadians(b.longitude - a.longitude)
        val aVal = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(a.latitude)) * cos(Math.toRadians(b.latitude)) *
                sin(dLng / 2).pow(2)
        val c = 2 * atan2(sqrt(aVal), sqrt(1 - aVal))
        return r * c
    }
}
