package com.nammaraste.health.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.nammaraste.health.data.local.entities.DamageReport
import com.nammaraste.health.data.repository.ReportRepository
import com.nammaraste.health.data.repository.RoadRepository
import com.nammaraste.health.util.DistanceCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val roadRepository: RoadRepository,
    private val reportRepository: ReportRepository
) : ViewModel() {

    private val _currentStep = MutableStateFlow(1)
    val currentStep = _currentStep.asStateFlow()

    private val _photoPath = MutableStateFlow("")
    val photoPath = _photoPath.asStateFlow()

    private val _location = MutableStateFlow<LatLng?>(null)
    val location = _location.asStateFlow()

    private val _selectedRoadId = MutableStateFlow(-1)
    val selectedRoadId = _selectedRoadId.asStateFlow()

    private val _damageType = MutableStateFlow("")
    val damageType = _damageType.asStateFlow()

    private val _severity = MutableStateFlow("")
    val severity = _severity.asStateFlow()

    private val _description = MutableStateFlow("")
    val description = _description.asStateFlow()

    private val _reporterName = MutableStateFlow("")
    val reporterName = _reporterName.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting = _isSubmitting.asStateFlow()

    private val _submitSuccess = MutableSharedFlow<Boolean>()
    val submitSuccess = _submitSuccess.asSharedFlow()

    val allRoads = roadRepository.getAllRoads()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun nextStep() {
        if (_currentStep.value < 3) {
            _currentStep.value += 1
        }
    }

    fun prevStep() {
        if (_currentStep.value > 1) {
            _currentStep.value -= 1
        }
    }

    fun onPhotoCaptured(path: String) {
        _photoPath.value = path
        if (path.isNotEmpty()) {
            nextStep()
        }
    }

    fun onLocationDetected(latLng: LatLng) {
        _location.value = latLng
        // Auto-select nearest road if none selected and roads are loaded
        if (_selectedRoadId.value == -1 && allRoads.value.isNotEmpty()) {
            val nearest = allRoads.value.minByOrNull { road ->
                DistanceCalculator.haversineDistanceKm(
                    latLng, 
                    LatLng((road.startLat + road.endLat) / 2, (road.startLng + road.endLng) / 2)
                )
            }
            nearest?.let { _selectedRoadId.value = it.roadId }
        }
    }

    fun onRoadSelected(roadId: Int) {
        _selectedRoadId.value = roadId
    }

    fun onDamageTypeSelected(type: String) {
        _damageType.value = type
    }

    fun onSeveritySelected(severity: String) {
        _severity.value = severity
    }

    fun onDescriptionChange(desc: String) {
        _description.value = desc
    }

    fun onReporterNameChange(name: String) {
        _reporterName.value = name
    }

    fun submitReport() {
        if (_reporterName.value.isBlank() || _damageType.value.isBlank() || _severity.value.isBlank() || _selectedRoadId.value == -1) {
            return
        }

        viewModelScope.launch {
            _isSubmitting.value = true
            val report = DamageReport(
                roadId = _selectedRoadId.value,
                latitude = _location.value?.latitude ?: 0.0,
                longitude = _location.value?.longitude ?: 0.0,
                damageType = _damageType.value,
                severity = _severity.value,
                description = _description.value,
                photoPath = _photoPath.value,
                reporterName = _reporterName.value,
                reportTimestamp = System.currentTimeMillis()
            )
            reportRepository.submitReport(report)
            _isSubmitting.value = false
            _submitSuccess.emit(true)
        }
    }
}
