package com.nammaraste.health.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "roads")
data class Road(
  @PrimaryKey(autoGenerate = true) val roadId: Int = 0,
  val roadName: String,
  val talukaName: String,
  val districtName: String,
  val contractorName: String,
  val contractorPhone: String,
  val contractorCompany: String,
  val warrantyStartDate: Long,
  val warrantyEndDate: Long,
  val totalLengthKm: Double,
  val startLat: Double,
  val startLng: Double,
  val endLat: Double,
  val endLng: Double,
  val polylinePoints: String, // JSON array of LatLng pairs
  val healthScore: Int = 100,
  val constructionYear: Int,
  val roadType: String // Gravel / Asphalt / Concrete
)
