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

@HiltViewModel
class MapViewModel @Inject constructor(
    private val roadRepository: RoadRepository,
    private val reportRepository: ReportRepository
) : ViewModel() {

    val allRoads = roadRepository.getAllRoads()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allReports = reportRepository.getAllReports()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _showReportsOnly = MutableStateFlow(false)
    val showReportsOnly = _showReportsOnly.asStateFlow()

    fun toggleReportsOnly(show: Boolean) {
        _showReportsOnly.value = show
    }
}
