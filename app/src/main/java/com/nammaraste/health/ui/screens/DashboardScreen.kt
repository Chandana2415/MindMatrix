package com.nammaraste.health.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nammaraste.health.ui.components.*
import com.nammaraste.health.ui.viewmodels.DashboardViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    onRoadClick: (Int) -> Unit,
    onReportClick: () -> Unit,
    onSeeAllRoads: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val recentRoads by viewModel.recentRoads.collectAsStateWithLifecycle()
    val recentReports by viewModel.recentReports.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onReportClick,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Report Damage") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ShimmerBox(modifier = Modifier.fillMaxWidth().height(120.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ShimmerBox(modifier = Modifier.weight(1f).height(100.dp))
                    ShimmerBox(modifier = Modifier.weight(1f).height(100.dp))
                }
                ShimmerBox(modifier = Modifier.fillMaxWidth().height(150.dp))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                item {
                    GreetingCard()
                }

                item {
                    StatsSection(
                        totalRoads = uiState.totalRoads,
                        activeReports = uiState.activeReports,
                        inWarranty = uiState.inWarranty,
                        avgHealthScore = uiState.avgHealthScore
                    )
                }

                item {
                    SectionHeader(
                        title = "Road Health Overview",
                        onSeeAllClick = onSeeAllRoads
                    )
                }

                item {
                    if (recentRoads.isEmpty()) {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(3) {
                                ShimmerBox(modifier = Modifier.width(220.dp).height(140.dp))
                            }
                        }
                    } else {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.height(160.dp)
                        ) {
                            items(recentRoads) { road ->
                                RoadHealthCard(
                                    road = road,
                                    onClick = { onRoadClick(road.roadId) },
                                    modifier = Modifier.width(220.dp).height(140.dp)
                                )
                            }
                        }
                    }
                }

                item {
                    SectionHeader(title = "Recent Damage Reports")
                }

                if (recentReports.isEmpty()) {
                    item {
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            repeat(3) {
                                ShimmerBox(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(80.dp)
                                        .padding(vertical = 6.dp)
                                )
                            }
                        }
                    }
                } else {
                    items(recentReports) { report ->
                        val roadName by viewModel.getRoadName(report.roadId).collectAsStateWithLifecycle("Loading...")
                        DamageReportCard(
                            report = report,
                            roadName = roadName,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            onClick = { onRoadClick(report.roadId) }
                        )
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun GreetingCard() {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Dharwad Taluka",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "Road Health Dashboard",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Icon(
                    imageVector = Icons.Default.HealthAndSafety,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault()).format(Date()),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
fun StatsSection(
    totalRoads: Int,
    activeReports: Int,
    inWarranty: Int,
    avgHealthScore: Int
) {
    Column(modifier = Modifier.padding(horizontal = 12.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            StatCard(
                label = "Total Roads",
                value = totalRoads.toString(),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "Active Reports",
                value = activeReports.toString(),
                modifier = Modifier.weight(1f),
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            StatCard(
                label = "In Warranty",
                value = inWarranty.toString(),
                modifier = Modifier.weight(1f),
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
            StatCard(
                label = "Avg Health Score",
                value = "$avgHealthScore%",
                modifier = Modifier.weight(1f),
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                showTrend = true
            )
        }
    }
}
