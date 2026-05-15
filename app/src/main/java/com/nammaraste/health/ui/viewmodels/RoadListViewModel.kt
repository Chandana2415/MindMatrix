package com.nammaraste.health.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nammaraste.health.data.local.entities.Road
import com.nammaraste.health.data.repository.RoadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class RoadListViewModel @Inject constructor(
    private val roadRepository: RoadRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _filterChip = MutableStateFlow("All")
    val filterChip = _filterChip.asStateFlow()

    val filteredRoads = combine(
        roadRepository.getAllRoads(),
        _searchQuery,
        _filterChip
    ) { roads, query, filter ->
        roads
            .filter { road ->
                query.isBlank() || 
                road.roadName.contains(query, ignoreCase = true) ||
                road.talukaName.contains(query, ignoreCase = true)
            }
            .filter { road ->
                when (filter) {
                    "Healthy" -> road.healthScore >= 80
                    "Warning" -> road.healthScore in 50..79
                    "Critical" -> road.healthScore < 50
                    "Active Warranty" -> road.warrantyEndDate > System.currentTimeMillis()
                    else -> true
                }
            }
            .sortedBy { it.roadName }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onFilterChipChange(newFilter: String) {
        _filterChip.value = newFilter
    }
}
