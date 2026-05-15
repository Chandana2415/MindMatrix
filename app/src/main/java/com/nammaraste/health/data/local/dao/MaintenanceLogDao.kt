package com.nammaraste.health.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nammaraste.health.data.local.entities.MaintenanceLog
import kotlinx.coroutines.flow.Flow

@Dao
interface MaintenanceLogDao {
    @Query("SELECT * FROM maintenance_logs WHERE roadId = :roadId ORDER BY logDate DESC")
    fun getLogsForRoad(roadId: Int): Flow<List<MaintenanceLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: MaintenanceLog)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(logs: List<MaintenanceLog>)
}
