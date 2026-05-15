package com.nammaraste.health.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nammaraste.health.data.local.entities.Road
import com.nammaraste.health.data.repository.RoadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class ContractorProfileViewModel @Inject constructor(
    private val roadRepository: RoadRepository
) : ViewModel() {

    private val _contractorName = MutableStateFlow("")

    val contractorRoads: StateFlow<List<Road>> = _contractorName
        .flatMapLatest { name ->
            roadRepository.getAllRoads().map { roads ->
                roads.filter { it.contractorName == name }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val stats: StateFlow<ContractorStats> = contractorRoads.map { roads ->
        if (roads.isEmpty()) return@map ContractorStats()
        
        val avgScore = roads.map { it.healthScore }.average().toInt()
        val activeWarranties = roads.count { it.warrantyEndDate > System.currentTimeMillis() }
        
        ContractorStats(
            roadsBuilt = roads.size,
            avgHealthScore = avgScore,
            activeWarranties = activeWarranties,
            rating = when {
                avgScore >= 80 -> 5
                avgScore >= 60 -> 4
                avgScore >= 40 -> 3
                avgScore >= 20 -> 2
                else -> 1
            }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ContractorStats())

    fun setContractorName(name: String) {
        _contractorName.value = name
    }
}

data class ContractorStats(
    val roadsBuilt: Int = 0,
    val avgHealthScore: Int = 0,
    val activeWarranties: Int = 0,
    val rating: Int = 0
)
