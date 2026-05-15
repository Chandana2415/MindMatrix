package com.nammaraste.health.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "maintenance_logs")
data class MaintenanceLog(
  @PrimaryKey(autoGenerate = true) val logId: Int = 0,
  val roadId: Int,
  val logDate: Long,
  val workDescription: String,
  val workType: String, // Patching / Resurfacing / DrainCleaning / Inspection
  val agencyName: String,
  val costEstimate: Double
)
