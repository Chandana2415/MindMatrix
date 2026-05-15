package com.nammaraste.health.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nammaraste.health.data.local.dao.DamageReportDao
import com.nammaraste.health.data.local.dao.MaintenanceLogDao
import com.nammaraste.health.data.local.dao.RoadDao
import com.nammaraste.health.data.local.entities.DamageReport
import com.nammaraste.health.data.local.entities.MaintenanceLog
import com.nammaraste.health.data.local.entities.Road

@Database(
    entities = [Road::class, DamageReport::class, MaintenanceLog::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun roadDao(): RoadDao
    abstract fun reportDao(): DamageReportDao
    abstract fun logDao(): MaintenanceLogDao
}
