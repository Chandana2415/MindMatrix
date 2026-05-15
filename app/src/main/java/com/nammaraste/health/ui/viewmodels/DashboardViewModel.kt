package com.nammaraste.health.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nammaraste.health.data.local.entities.DamageReport
import com.nammaraste.health.data.local.entities.Road
import com.nammaraste.health.data.repository.ReportRepository
import com.nammaraste.health.data.repository.RoadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class DashboardUiState(
    val totalRoads: Int = 0,
    val activeReports: Int = 0,
    val inWarranty: Int = 0,
    val avgHealthScore: Int = 0,
    val isLoading: Boolean = true
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val roadRepository: RoadRepository,
    private val reportRepository: ReportRepository
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = combine(
        roadRepository.getTotalCount(),
        reportRepository.getActiveCount(),
        roadRepository.getRoadsInWarranty(),
        roadRepository.getAvgHealthScore()
    ) { t, a, w, s ->
        DashboardUiState(
            totalRoads = t,
            activeReports = a,
            inWarranty = w,
            avgHealthScore = s?.toInt() ?: 0,
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardUiState())

    val recentRoads = roadRepository.getAllRoads()
        .map { roads -> roads.sortedBy { it.healthScore } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentReports = reportRepository.getAllReports()
        .map { it.take(5) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getRoadName(roadId: Int): Flow<String> {
        return roadRepository.getRoadById(roadId).map { it?.roadName ?: "Unknown Road" }
    }
}
