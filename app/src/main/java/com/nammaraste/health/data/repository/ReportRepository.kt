package com.nammaraste.health.data.repository

import com.nammaraste.health.data.local.dao.DamageReportDao
import com.nammaraste.health.data.local.dao.RoadDao
import com.nammaraste.health.data.local.entities.DamageReport
import com.nammaraste.health.domain.HealthScoreComputer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportRepository @Inject constructor(
    private val reportDao: DamageReportDao,
    private val roadDao: RoadDao,
    private val healthScoreComputer: HealthScoreComputer
) {
    fun getAllReports(): Flow<List<DamageReport>> = reportDao.getAllReports()
    fun getReportsForRoad(id: Int): Flow<List<DamageReport>> = reportDao.getReportsForRoad(id)
    fun getActiveCount(): Flow<Int> = reportDao.getActiveReportCount()

    suspend fun submitReport(report: DamageReport) {
        reportDao.insertReport(report)
        val road = roadDao.getRoadById(report.roadId).first() ?: return
        val allReports = reportDao.getRecentReportsForRoad(
            report.roadId,
            System.currentTimeMillis() - 90L * 24 * 60 * 60 * 1000
        )
        val newScore = healthScoreComputer.computeScore(allReports, road.totalLengthKm)
        roadDao.updateRoad(road.copy(healthScore = newScore))
    }
}
