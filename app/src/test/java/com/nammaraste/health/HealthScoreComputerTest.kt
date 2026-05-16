package com.nammaraste.health

import com.nammaraste.health.data.local.entities.DamageReport
import com.nammaraste.health.domain.HealthScoreComputer
import org.junit.Assert.assertEquals
import org.junit.Test

class HealthScoreComputerTest {

    private val computer = HealthScoreComputer()

    @Test
    fun `test perfect score with no reports`() {
        val score = computer.computeScore(emptyList(), 1.0)
        assertEquals(100, score)
    }

    @Test
    fun `test severe damage reduces score`() {
        val reports = listOf(
            DamageReport(
                reportId = 1,
                roadId = 1,
                reportTimestamp = System.currentTimeMillis(),
                latitude = 0.0,
                longitude = 0.0,
                damageType = "Pothole",
                severity = "Severe",
                description = "Big pothole",
                photoPath = "",
                reporterName = "User",
                isResolved = false
            )
        )
        // Severe penalty = 15. Score = 100 - 15 = 85
        val score = computer.computeScore(reports, 1.0)
        assertEquals(85, score)
    }

    @Test
    fun `test multiple reports on long road`() {
        val reports = listOf(
            DamageReport(1, 1, System.currentTimeMillis(), 0.0, 0.0, "Pothole", "Severe", "", "", "", false), // 15
            DamageReport(2, 1, System.currentTimeMillis(), 0.0, 0.0, "Crack", "Moderate", "", "", "", false)  // 8
        )
        // Total penalty = 23. Road length = 10km. Per km = 2. Score = 98
        val score = computer.computeScore(reports, 10.0)
        assertEquals(98, score)
    }

    @Test
    fun `test resolved reports don't count`() {
        val reports = listOf(
            DamageReport(1, 1, System.currentTimeMillis(), 0.0, 0.0, "Pothole", "Severe", "", "", "", true)
        )
        val score = computer.computeScore(reports, 1.0)
        assertEquals(100, score)
    }
}
