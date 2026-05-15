package com.nammaraste.health.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nammaraste.health.data.local.entities.DamageReport
import kotlinx.coroutines.flow.Flow

@Dao
interface DamageReportDao {
    @Query("SELECT * FROM damage_reports ORDER BY reportTimestamp DESC")
    fun getAllReports(): Flow<List<DamageReport>>

    @Query("SELECT * FROM damage_reports WHERE roadId = :roadId ORDER BY reportTimestamp DESC")
    fun getReportsForRoad(roadId: Int): Flow<List<DamageReport>>

    @Query("SELECT COUNT(*) FROM damage_reports WHERE isResolved = 0")
    fun getActiveReportCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: DamageReport)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reports: List<DamageReport>)

    @Update
    suspend fun updateReport(report: DamageReport)

    @Query("SELECT * FROM damage_reports WHERE roadId = :roadId AND reportTimestamp > :since")
    suspend fun getRecentReportsForRoad(roadId: Int, since: Long): List<DamageReport>
}
