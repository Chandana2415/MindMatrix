package com.nammaraste.health.data.repository

import com.nammaraste.health.data.local.dao.DamageReportDao
import com.nammaraste.health.data.local.dao.RoadDao
import com.nammaraste.health.data.local.entities.Road
import com.nammaraste.health.domain.HealthScoreComputer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoadRepository @Inject constructor(
    private val roadDao: RoadDao,
    private val reportDao: DamageReportDao,
    private val scorer: HealthScoreComputer
) {
    fun getAllRoads(): Flow<List<Road>> = roadDao.getAllRoads()
    fun getRoadById(id: Int): Flow<Road?> = roadDao.getRoadById(id)
    fun searchRoads(query: String): Flow<List<Road>> = roadDao.searchRoads(query)
    fun getAvgHealthScore(): Flow<Double?> = roadDao.getAvgHealthScore()
    fun getTotalCount(): Flow<Int> = roadDao.getTotalRoadCount()
    fun getRoadsInWarranty(): Flow<Int> = roadDao.getRoadsInWarranty(System.currentTimeMillis())

    suspend fun refreshHealthScore(roadId: Int, lengthKm: Double) {
        val ninetyDaysAgo = System.currentTimeMillis() - 90L * 24 * 60 * 60 * 1000
        val reports = reportDao.getRecentReportsForRoad(roadId, ninetyDaysAgo)
        val score = scorer.computeScore(reports, lengthKm)
        val road = roadDao.getRoadById(roadId).first()
        road?.let { roadDao.updateRoad(it.copy(healthScore = score)) }
    }
}
