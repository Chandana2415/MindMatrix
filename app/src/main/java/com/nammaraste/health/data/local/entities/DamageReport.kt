package com.nammaraste.health.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "damage_reports")
data class DamageReport(
  @PrimaryKey(autoGenerate = true) val reportId: Int = 0,
  val roadId: Int,
  val reportTimestamp: Long = System.currentTimeMillis(),
  val latitude: Double,
  val longitude: Double,
  val damageType: String, // Pothole / Crack / Waterlogging / DrainClog / Other
  val severity: String,   // Minor / Moderate / Severe
  val description: String,
  val photoPath: String,
  val reporterName: String,
  val isResolved: Boolean = false,
  val resolvedTimestamp: Long? = null
)
