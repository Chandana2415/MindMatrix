package com.nammaraste.health.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nammaraste.health.data.local.dao.DamageReportDao
import com.nammaraste.health.data.local.dao.MaintenanceLogDao
import com.nammaraste.health.data.local.entities.DamageReport
import com.nammaraste.health.data.local.entities.MaintenanceLog
import com.nammaraste.health.data.local.entities.Road
import com.nammaraste.health.data.repository.ReportRepository
import com.nammaraste.health.data.repository.RoadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class RoadDetailViewModel @Inject constructor(
    private val roadRepository: RoadRepository,
    private val reportRepository: ReportRepository,
    private val reportDao: DamageReportDao,
    private val logDao: MaintenanceLogDao
) : ViewModel() {

    private val _roadId = MutableStateFlow<Int>(-1)

    val road: StateFlow<Road?> = _roadId
        .flatMapLatest { id -> roadRepository.getRoadById(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val reports: StateFlow<List<DamageReport>> = _roadId
        .flatMapLatest { id -> reportRepository.getReportsForRoad(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val logs: StateFlow<List<MaintenanceLog>> = _roadId
        .flatMapLatest { id -> logDao.getLogsForRoad(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setRoadId(id: Int) {
        _roadId.value = id
    }

    fun markResolved(reportId: Int) {
        viewModelScope.launch {
            val allReports = reportRepository.getAllReports().first()
            val report = allReports.find { it.reportId == reportId }
            report?.let {
                val updatedReport = it.copy(
                    isResolved = true,
                    resolvedTimestamp = System.currentTimeMillis()
                )
                reportDao.updateReport(updatedReport)
                // Trigger health score refresh
                val currentRoad = road.value
                currentRoad?.let { r ->
                    roadRepository.refreshHealthScore(r.roadId, r.totalLengthKm)
                }
            }
        }
    }
}
