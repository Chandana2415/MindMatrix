package com.nammaraste.health.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.accompanist.permissions.*
import android.Manifest
import com.nammaraste.health.data.local.entities.Road
import com.nammaraste.health.ui.components.HealthScoreGauge
import com.nammaraste.health.ui.theme.healthColor
import com.nammaraste.health.ui.viewmodels.MapViewModel
import com.nammaraste.health.util.PolylineParser
import com.nammaraste.health.util.toRelativeTime
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    onRoadClick: (Int) -> Unit,
    onReportDamageClick: (Int) -> Unit,
    viewModel: MapViewModel = hiltViewModel()
) {
    val allRoads by viewModel.allRoads.collectAsState()
    val allReports by viewModel.allReports.collectAsState()
    val showReportsOnly by viewModel.showReportsOnly.collectAsState()

    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    
    val sheetState = rememberModalBottomSheetState()
    var selectedRoad by remember { mutableStateOf<Road?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }

    val karnatakaCenter = LatLng(15.3173, 75.7139)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(karnatakaCenter, 9f)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = locationPermissionState.status.isGranted),
            uiSettings = MapUiSettings(zoomControlsEnabled = true)
        ) {
            if (!showReportsOnly) {
                allRoads.forEach { road ->
                    val points = PolylineParser.parsePolyline(road.polylinePoints)
                    if (points.isNotEmpty()) {
                        Polyline(
                            points = points,
                            color = healthColor(road.healthScore),
                            width = 8f,
                            clickable = true,
                            onClick = {
                                selectedRoad = road
                                showBottomSheet = true
                            }
                        )
                    }
                }
            }

            allReports.filter { !it.isResolved }.forEach { report ->
                Marker(
                    state = MarkerState(position = LatLng(report.latitude, report.longitude)),
                    title = report.damageType,
                    snippet = "${report.severity} • ${report.reportTimestamp.toRelativeTime()}",
                    icon = severityMarkerIcon(report.severity)
                )
            }
        }

        // Legend
        Card(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                LegendRow(color = Color(0xFF2E7D32), label = "Healthy (80-100)")
                LegendRow(color = Color(0xFFF57F17), label = "Warning (50-79)")
                LegendRow(color = Color(0xFFC62828), label = "Critical (<50)")
            }
        }

        // Filter Toggle
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f), RoundedCornerShape(24.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = !showReportsOnly,
                onClick = { viewModel.toggleReportsOnly(false) },
                label = { Text("All Roads") }
            )
            FilterChip(
                selected = showReportsOnly,
                onClick = { viewModel.toggleReportsOnly(true) },
                label = { Text("Reports Only") }
            )
        }

        if (showBottomSheet && selectedRoad != null) {
            val road = selectedRoad!!
            val roadReports = allReports.count { it.roadId == road.roadId }
            
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp, start = 24.dp, end = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = road.roadName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${road.talukaName}, ${road.districtName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        HealthScoreGauge(score = road.healthScore, modifier = Modifier.size(100.dp))
                        
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = roadReports.toString(),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Active Reports",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedButton(
                            onClick = {
                                showBottomSheet = false
                                onRoadClick(road.roadId)
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("View Details")
                        }
                        Button(
                            onClick = {
                                showBottomSheet = false
                                onReportDamageClick(road.roadId)
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Report Damage")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendRow(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
        Box(modifier = Modifier.size(12.dp).background(color, RoundedCornerShape(2.dp)))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall)
    }
}

private fun severityMarkerIcon(severity: String): BitmapDescriptor {
    return BitmapDescriptorFactory.defaultMarker(
        when (severity) {
            "Severe" -> BitmapDescriptorFactory.HUE_RED
            "Moderate" -> BitmapDescriptorFactory.HUE_ORANGE
            else -> BitmapDescriptorFactory.HUE_GREEN
        }
    )
}
