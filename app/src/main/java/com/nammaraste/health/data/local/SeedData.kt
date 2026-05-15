package com.nammaraste.health.data.local

import com.nammaraste.health.data.local.entities.DamageReport
import com.nammaraste.health.data.local.entities.MaintenanceLog
import com.nammaraste.health.data.local.entities.Road
import com.nammaraste.health.domain.HealthScoreComputer
import com.nammaraste.health.util.PolylineParser
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Random

object SeedData {
    private val random = Random()

    suspend fun populate(db: AppDatabase) {
        val roadDao = db.roadDao()
        val reportDao = db.reportDao()
        val logDao = db.logDao()

        if (roadDao.getTotalRoadCount().first() > 0) return

        val now = System.currentTimeMillis()
        val dayMillis = 24 * 60 * 60 * 1000L
        
        fun dateToMillis(year: Int, month: Int, day: Int) = 
            LocalDate.of(year, month, day).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

        val roads = listOf(
            Road(
                roadName = "Kalghatgi-Shigli Road",
                talukaName = "Kalghatgi",
                districtName = "Dharwad",
                contractorName = "Sri Basaveshwara Constructions",
                contractorPhone = "+91-9845012345",
                contractorCompany = "Sri Basaveshwara Constructions",
                warrantyStartDate = dateToMillis(2021, 3, 1),
                warrantyEndDate = dateToMillis(2026, 3, 1),
                totalLengthKm = 12.4,
                startLat = 15.1823, startLng = 74.9765,
                endLat = 15.2341, endLng = 75.0123,
                polylinePoints = generatePolyline(15.1823, 74.9765, 15.2341, 75.0123),
                constructionYear = 2021,
                roadType = "Asphalt"
            ),
            Road(
                roadName = "Annigeri-Navalgund Link Road",
                talukaName = "Navalgund",
                districtName = "Dharwad",
                contractorName = "Kaveri Road Works Pvt Ltd",
                contractorPhone = "+91-9900123456",
                contractorCompany = "Kaveri Road Works Pvt Ltd",
                warrantyStartDate = dateToMillis(2022, 5, 15),
                warrantyEndDate = dateToMillis(2027, 5, 15),
                totalLengthKm = 8.7,
                startLat = 15.4231, startLng = 75.3412,
                endLat = 15.4876, endLng = 75.3987,
                polylinePoints = generatePolyline(15.4231, 75.3412, 15.4876, 75.3987),
                constructionYear = 2022,
                roadType = "Concrete"
            ),
            Road(
                roadName = "Hubli-Gabbur Village Road",
                talukaName = "Hubli",
                districtName = "Dharwad",
                contractorName = "Deccan Infrastructure Ltd",
                contractorPhone = "+91-9741234567",
                contractorCompany = "Deccan Infrastructure Ltd",
                warrantyStartDate = dateToMillis(2020, 10, 10),
                warrantyEndDate = now + 45 * dayMillis,
                totalLengthKm = 5.2,
                startLat = 15.3647, startLng = 75.1234,
                endLat = 15.3891, endLng = 75.1567,
                polylinePoints = generatePolyline(15.3647, 75.1234, 15.3891, 75.1567),
                constructionYear = 2020,
                roadType = "Gravel"
            ),
            Road(
                roadName = "Dharwad-Kalaghatgi Bypass",
                talukaName = "Dharwad",
                districtName = "Dharwad",
                contractorName = "Sharavathi Builders",
                contractorPhone = "+91-9886543210",
                contractorCompany = "Sharavathi Builders",
                warrantyStartDate = dateToMillis(2019, 1, 1),
                warrantyEndDate = now - 20 * dayMillis,
                totalLengthKm = 18.1,
                startLat = 15.4589, startLng = 74.9876,
                endLat = 15.1823, endLng = 74.9765,
                polylinePoints = generatePolyline(15.4589, 74.9876, 15.1823, 74.9765),
                constructionYear = 2019,
                roadType = "Asphalt"
            ),
            Road(
                roadName = "Gadag-Lakkundi Heritage Road",
                talukaName = "Gadag",
                districtName = "Gadag",
                contractorName = "Tungabhadra Road Corp",
                contractorPhone = "+91-9972345678",
                contractorCompany = "Tungabhadra Road Corp",
                warrantyStartDate = dateToMillis(2023, 1, 1),
                warrantyEndDate = dateToMillis(2028, 1, 1),
                totalLengthKm = 6.8,
                startLat = 15.4167, startLng = 75.6234,
                endLat = 15.4523, endLng = 75.6789,
                polylinePoints = generatePolyline(15.4167, 75.6234, 15.4523, 75.6789),
                constructionYear = 2023,
                roadType = "Concrete"
            ),
            Road(
                roadName = "Ron-Gajendragad Road",
                talukaName = "Ron",
                districtName = "Gadag",
                contractorName = "Malaprabha Constructions",
                contractorPhone = "+91-9845678901",
                contractorCompany = "Malaprabha Constructions",
                warrantyStartDate = dateToMillis(2021, 6, 20),
                warrantyEndDate = dateToMillis(2026, 6, 20),
                totalLengthKm = 14.3,
                startLat = 15.6789, startLng = 75.7123,
                endLat = 15.7234, endLng = 75.7876,
                polylinePoints = generatePolyline(15.6789, 75.7123, 15.7234, 75.7876),
                constructionYear = 2021,
                roadType = "Gravel"
            ),
            Road(
                roadName = "Kundgol-Dharwad Rural Link",
                talukaName = "Kundgol",
                districtName = "Dharwad",
                contractorName = "Sri Basaveshwara Constructions",
                contractorPhone = "+91-9845012345",
                contractorCompany = "Sri Basaveshwara Constructions",
                warrantyStartDate = dateToMillis(2022, 11, 1),
                warrantyEndDate = dateToMillis(2027, 11, 1),
                totalLengthKm = 9.6,
                startLat = 15.2567, startLng = 75.2341,
                endLat = 15.3123, endLng = 75.2876,
                polylinePoints = generatePolyline(15.2567, 75.2341, 15.3123, 75.2876),
                constructionYear = 2022,
                roadType = "Asphalt"
            ),
            Road(
                roadName = "Nargund-Shirhatti Connector",
                talukaName = "Nargund",
                districtName = "Gadag",
                contractorName = "Kaveri Road Works Pvt Ltd",
                contractorPhone = "+91-9900123456",
                contractorCompany = "Kaveri Road Works Pvt Ltd",
                warrantyStartDate = dateToMillis(2020, 2, 1),
                warrantyEndDate = now + 10 * dayMillis,
                totalLengthKm = 11.2,
                startLat = 15.7234, startLng = 75.3876,
                endLat = 15.7678, endLng = 75.4312,
                polylinePoints = generatePolyline(15.7234, 75.3876, 15.7678, 75.4312),
                constructionYear = 2020,
                roadType = "Concrete"
            )
        )
        roadDao.insertAll(roads)
        
        // Fetch inserted roads to get correct IDs
        val insertedRoads = roadDao.getAllRoads().first()

        val damageTypes = listOf("Pothole", "Crack", "Waterlogging", "Drain Clog", "Other")
        val severities = listOf("Minor", "Moderate", "Severe")
        val names = listOf("Arjun Singh", "Priya Kulkarni", "Suresh Kumar", "Anita Desai", "Ravi Patil")

        val reports = mutableListOf<DamageReport>()
        for (i in 0 until 16) {
            val road = insertedRoads[i % insertedRoads.size]
            val points = PolylineParser.parsePolyline(road.polylinePoints)
            val point = points[random.nextInt(points.size)]
            
            reports.add(
                DamageReport(
                    roadId = road.roadId,
                    latitude = point.latitude + (random.nextDouble() - 0.5) * 0.001,
                    longitude = point.longitude + (random.nextDouble() - 0.5) * 0.001,
                    damageType = damageTypes[random.nextInt(damageTypes.size)],
                    severity = if (i < 6) "Severe" else if (i < 12) "Moderate" else "Minor",
                    description = "Automated sensor detection confirmed by citizen report.",
                    photoPath = "",
                    reporterName = names[random.nextInt(names.size)],
                    reportTimestamp = now - random.nextInt(60).toLong() * dayMillis,
                    isResolved = false
                )
            )
        }
        reportDao.insertAll(reports)

        val logs = mutableListOf<MaintenanceLog>()
        for (i in 0 until 10) {
            val road = insertedRoads[random.nextInt(insertedRoads.size)]
            logs.add(
                MaintenanceLog(
                    roadId = road.roadId,
                    logDate = now - random.nextInt(180).toLong() * dayMillis,
                    workDescription = "Routine maintenance and surface check completed successfully.",
                    workType = listOf("Patching", "Resurfacing", "Drain Cleaning", "Inspection")[random.nextInt(4)],
                    agencyName = "PWD Karnataka",
                    costEstimate = (random.nextInt(50) + 10) * 1000.0
                )
            )
        }
        logDao.insertAll(logs)

        // Refresh all health scores
        refreshAllHealthScores(db, HealthScoreComputer())
    }

    private suspend fun refreshAllHealthScores(db: AppDatabase, scorer: HealthScoreComputer) {
        val cutoff = System.currentTimeMillis() - 90L * 24 * 60 * 60 * 1000
        val roads = db.roadDao().getAllRoads().first()
        roads.forEach { road ->
            val reports = db.reportDao().getReportsForRoad(road.roadId).first()
            val recentReports = reports.filter { it.reportTimestamp > cutoff }
            val score = scorer.computeScore(recentReports, road.totalLengthKm)
            db.roadDao().updateRoad(road.copy(healthScore = score))
        }
    }

    private fun generatePolyline(sLat: Double, sLng: Double, eLat: Double, eLng: Double): String {
        val points = mutableListOf<String>()
        for (i in 0..7) {
            val fraction = i / 7.0
            val lat = sLat + (eLat - sLat) * fraction
            val lng = sLng + (eLng - sLng) * fraction
            points.add("{\"lat\":$lat,\"lng\":$lng}")
        }
        return "[" + points.joinToString(",") + "]"
    }
}
