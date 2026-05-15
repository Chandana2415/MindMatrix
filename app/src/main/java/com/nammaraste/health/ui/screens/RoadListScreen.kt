package com.nammaraste.health.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nammaraste.health.ui.components.IllustratedEmptyState
import com.nammaraste.health.ui.components.RoadCard
import com.nammaraste.health.ui.components.ShimmerBox
import com.nammaraste.health.ui.viewmodels.RoadListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoadListScreen(
    onRoadClick: (Int) -> Unit,
    onBack: () -> Unit,
    viewModel: RoadListViewModel = hiltViewModel()
) {
    val roads by viewModel.filteredRoads.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filterChip by viewModel.filterChip.collectAsState()

    val filters = listOf("All", "Healthy", "Warning", "Critical", "Active Warranty", "Expired")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Road Network") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search roads or talukas...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                items(filters) { filter ->
                    FilterChip(
                        selected = filterChip == filter,
                        onClick = { viewModel.onFilterChipChange(filter) },
                        label = { Text(filter) }
                    )
                }
            }

            if (roads.isEmpty() && searchQuery.isEmpty()) {
                // Loading state simulation or actually empty
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(5) {
                        ShimmerBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            } else if (roads.isEmpty()) {
                IllustratedEmptyState(type = "no_results")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(roads) { road ->
                        RoadCard(
                            road = road,
                            onClick = { onRoadClick(road.roadId) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}
