package com.nammaraste.health.util

import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray

object PolylineParser {
    fun parsePolyline(json: String): List<LatLng> {
        return try {
            val arr = JSONArray(json)
            (0 until arr.length()).map { i ->
                val obj = arr.getJSONObject(i)
                LatLng(obj.getDouble("lat"), obj.getDouble("lng"))
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun roadMidpoint(points: List<LatLng>): LatLng {
        if (points.isEmpty()) return LatLng(15.3173, 75.7139)
        return points[points.size / 2]
    }
}
