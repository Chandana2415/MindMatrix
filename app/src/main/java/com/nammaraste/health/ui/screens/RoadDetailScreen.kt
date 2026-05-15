package com.nammaraste.health.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.nammaraste.health.data.local.entities.Road
import com.nammaraste.health.ui.components.*
import com.nammaraste.health.ui.theme.healthColor
import com.nammaraste.health.ui.viewmodels.RoadDetailViewModel
import com.nammaraste.health.util.PolylineParser
import com.nammaraste.health.util.toDateOnly

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoadDetailScreen(
    roadId: Int,
    onBack: () -> Unit,
    onReportClick: (Int) -> Unit,
    onContractorClick: (String) -> Unit,
    viewModel: RoadDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    
    LaunchedEffect(roadId) {
        viewModel.setRoadId(roadId)
    }

    val road by viewModel.road.collectAsState()
    val reports by viewModel.reports.collectAsState()
    val logs by viewModel.logs.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(road?.roadName ?: "Road Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            if (road != null) {
                Surface(tonalElevation = 8.dp) {
                    Button(
                        onClick = { onReportClick(roadId) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Report Damage on This Road")
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (road == null) {
                item { RoadDetailShimmer() }
            } else {
                val currentRoad = road!!
                item {
                    RoadHeroSection(currentRoad)
                }

                item {
                    MapPreviewCard(currentRoad, reports)
                }

                item {
                    HealthStatusSection(currentRoad.healthScore, reports.size)
                }

                item {
                    ContractorInfoCard(
                        name = currentRoad.contractorName,
                        company = currentRoad.contractorCompany,
                        phone = currentRoad.contractorPhone,
                        warrantyStart = currentRoad.warrantyStartDate,
                        warrantyEnd = currentRoad.warrantyEndDate,
                        onContractorClick = { onContractorClick(currentRoad.contractorName) },
                        onCallClick = {
                            Toast.makeText(context, "Calling ${currentRoad.contractorPhone}...", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                item {
                    TabRow(selectedTabIndex = selectedTab) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Damage Reports") }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Maintenance Log") }
                        )
                    }
                }

                if (selectedTab == 0) {
                    if (reports.isEmpty()) {
                        item {
                            IllustratedEmptyState(
                                type = "no_reports",
                                modifier = Modifier.height(300.dp)
                            )
                        }
                    } else {
                        items(reports) { report ->
                            DamageReportCard(
                                report = report,
                                roadName = currentRoad.roadName,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                onResolve = { viewModel.markResolved(it) }
                            )
                        }
                    }
                } else {
                    if (logs.isEmpty()) {
                        item {
                            Box(modifier = Modifier.height(200.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                                Text("No maintenance logs found.")
                            }
                        }
                    } else {
                        items(logs) { log ->
                            MaintenanceLogItem(log)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun RoadDetailShimmer() {
    Column(modifier = Modifier.padding(16.dp)) {
        ShimmerBox(modifier = Modifier.fillMaxWidth().height(120.dp), shape = RoundedCornerShape(16.dp))
        Spacer(modifier = Modifier.height(16.dp))
        ShimmerBox(modifier = Modifier.fillMaxWidth().height(240.dp), shape = RoundedCornerShape(16.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            ShimmerBox(modifier = Modifier.size(100.dp), shape = CircleShape)
            Spacer(modifier = Modifier.width(24.dp))
            Column {
                ShimmerBox(modifier = Modifier.width(120.dp).height(20.dp))
                Spacer(modifier = Modifier.height(8.dp))
                ShimmerBox(modifier = Modifier.width(180.dp).height(16.dp))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        ShimmerBox(modifier = Modifier.fillMaxWidth().height(150.dp), shape = RoundedCornerShape(16.dp))
    }
}

@Composable
fun RoadHeroSection(road: Road) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = road.roadName,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SuggestionChip(onClick = {}, label = { Text(road.roadType) })
                SuggestionChip(onClick = {}, label = { Text("${road.totalLengthKm} km") })
                SuggestionChip(onClick = {}, label = { Text("Built: ${road.constructionYear}") })
            }
            Text(
                text = "${road.talukaName}, ${road.districtName}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun MapPreviewCard(road: Road, reports: List<com.nammaraste.health.data.local.entities.DamageReport>) {
    val points = remember(road.polylinePoints) { PolylineParser.parsePolyline(road.polylinePoints) }
    val midpoint = remember(points) { PolylineParser.roadMidpoint(points) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(midpoint, 13f)
    }

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(240.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(zoomControlsEnabled = false, scrollGesturesEnabled = false)
            ) {
                Polyline(
                    points = points,
                    color = healthColor(road.healthScore),
                    width = 12f
                )
                reports.forEach { report ->
                    Marker(
                        state = MarkerState(position = LatLng(report.latitude, report.longitude)),
                        title = report.damageType
                    )
                }
            }
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Preview Mode",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
fun HealthStatusSection(score: Int, reportCount: Int) {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HealthScoreGauge(score = score, modifier = Modifier.size(100.dp))
        Spacer(modifier = Modifier.width(24.dp))
        Column {
            Text(
                text = "Health Status",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "$reportCount active damage reports",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Last updated: Just now",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun ContractorInfoCard(
    name: String,
    company: String,
    phone: String,
    warrantyStart: Long,
    warrantyEnd: Long,
    onContractorClick: () -> Unit,
    onCallClick: () -> Unit
) {
    val status = com.nammaraste.health.ui.theme.warrantyStatus(warrantyEnd)
    val statusColor = com.nammaraste.health.ui.theme.warrantyColor(status)
    
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (status == "Expiring Soon") {
                val daysLeft = (warrantyEnd - System.currentTimeMillis()) / 86400000
                Surface(
                    color = Color(0xFFFFF3CD),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "⚠️ Warranty expires in $daysLeft days!",
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF856404)
                    )
                }
            } else if (status == "Expired") {
                Surface(
                    color = Color(0xFFF8D7DA),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "❌ Warranty period has expired",
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF721C24)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = name.take(1).uppercase(),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(text = company, style = MaterialTheme.typography.bodySmall)
                }
                IconButton(onClick = onCallClick) {
                    Icon(Icons.Default.Phone, contentDescription = "Call", tint = MaterialTheme.colorScheme.primary)
                }
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Warranty Period", style = MaterialTheme.typography.labelSmall)
                    Text(
                        text = "${warrantyStart.toDateOnly()} - ${warrantyEnd.toDateOnly()}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = status,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = statusColor,
                        style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp)
                    )
                }
            }
            
            TextButton(
                onClick = onContractorClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("View Contractor Profile")
            }
        }
    }
}

@Composable
fun MaintenanceLogItem(log: com.nammaraste.health.data.local.entities.MaintenanceLog) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = log.logDate.toDateOnly(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = log.workType,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = log.workDescription,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Agency: ${log.agencyName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Cost: ₹${log.costEstimate.toInt()}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}
