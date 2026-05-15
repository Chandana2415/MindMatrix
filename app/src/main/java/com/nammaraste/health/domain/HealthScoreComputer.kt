package com.nammaraste.health.domain

import com.nammaraste.health.data.local.entities.DamageReport
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HealthScoreComputer @Inject constructor() {
    fun computeScore(reports: List<DamageReport>, roadLengthKm: Double): Int {
        val cutoff = System.currentTimeMillis() - (90L * 24 * 60 * 60 * 1000)
        val recent = reports.filter { it.reportTimestamp > cutoff && !it.isResolved }
        var penalty = 0
        recent.forEach { report ->
            val severityMultiplier = when (report.severity) {
                "Severe"   -> 15
                "Moderate" -> 8
                "Minor"    -> 3
                else       -> 3
            }
            penalty += severityMultiplier
        }
        val perKmPenalty = (penalty.toDouble() / maxOf(roadLengthKm, 1.0)).toInt()
        return (100 - perKmPenalty).coerceIn(0, 100)
    }
}
