package com.nammaraste.health.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nammaraste.health.data.local.entities.Road
import com.nammaraste.health.data.repository.RoadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val roadRepository: RoadRepository
) : ViewModel() {

    val sortedRoads: StateFlow<List<Road>> = roadRepository.getAllRoads()
        .map { roads -> roads.sortedByDescending { it.healthScore } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val topThree: StateFlow<List<Road>> = sortedRoads
        .map { it.take(3) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val needsAttention: StateFlow<List<Road>> = roadRepository.getAllRoads()
        .map { roads -> roads.sortedBy { it.healthScore }.take(3) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
