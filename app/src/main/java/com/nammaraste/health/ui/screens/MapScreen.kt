package com.nammaraste.health.ui.screens

import android.Manifest
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.nammaraste.health.R
import com.nammaraste.health.data.local.entities.Road
import com.nammaraste.health.data.local.entities.DamageReport
import com.nammaraste.health.ui.components.HealthScoreGauge
import com.nammaraste.health.ui.theme.healthColor
import com.nammaraste.health.ui.viewmodels.MapViewModel
import com.nammaraste.health.util.PolylineParser
import com.nammaraste.health.util.toRelativeTime
import kotlinx.coroutines.launch

/**
 * Senior Developer Implementation of Google Maps for Android (Jetpack Compose)
 * Implements: Interactive Markers, Info Windows, Coordinate Retrieval, and Responsive UI.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    onRoadClick: (Int) -> Unit,
    onReportDamageClick: (Int) -> Unit,
    viewModel: MapViewModel = hiltViewModel(),
    initialCenter: LatLng = LatLng(15.4589, 75.0078),
    initialZoom: Float = 11f
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // UI State from ViewModel
    val allRoads by viewModel.allRoads.collectAsState()
    val allReports by viewModel.allReports.collectAsState()
    val showReportsOnly by viewModel.showReportsOnly.collectAsState()

    // Map & Interaction State
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialCenter, initialZoom)
    }
    
    var selectedRoad by remember { mutableStateOf<Road?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.infrastructure_map), fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { viewModel.forceSeedData(); Toast.makeText(context, "Data Refreshed", Toast.LENGTH_SHORT).show() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                    IconButton(onClick = {
                        scope.launch { cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(initialCenter, initialZoom)) }
                    }) {
                        Icon(Icons.Default.MyLocation, contentDescription = "Recenter")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapLongClick = { latLng ->
                    // Coordinate Retrieval (Requirement 2.4)
                    Toast.makeText(context, "Coordinates: ${latLng.latitude}, ${latLng.longitude}", Toast.LENGTH_LONG).show()
                },
                properties = MapProperties(isMyLocationEnabled = locationPermissionState.status.isGranted),
                uiSettings = MapUiSettings(zoomControlsEnabled = false, myLocationButtonEnabled = true)
            ) {
                // Render Roads (Polylines)
                if (!showReportsOnly) {
                    allRoads.forEach { road ->
                        key(road.roadId) {
                            val points = remember(road.polylinePoints) { PolylineParser.parsePolyline(road.polylinePoints) }
                            if (points.isNotEmpty()) {
                                Polyline(
                                    points = points,
                                    color = healthColor(road.healthScore),
                                    width = 14f,
                                    clickable = true,
                                    onClick = { selectedRoad = road; showBottomSheet = true }
                                )
                            }
                        }
                    }
                }

                // Render Interactive Markers & Info Windows (Requirement 2.3)
                allReports.filter { !it.isResolved }.forEach { report ->
                    key(report.reportId) {
                        MarkerInfoWindow(
                            state = rememberMarkerState(position = LatLng(report.latitude, report.longitude)),
                            icon = severityMarkerIcon(report.severity),
                            title = report.damageType
                        ) { marker ->
                            // Custom Info Window
                            Card(
                                modifier = Modifier.padding(8.dp).width(200.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(text = report.damageType, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(text = "Severity: ${report.severity}", fontSize = 12.sp, color = Color.Gray)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Reported ${report.reportTimestamp.toRelativeTime()}", fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Legend Overlay
            MapLegendOverlay(Modifier.align(Alignment.TopEnd).padding(16.dp))

            // Filter Toggle
            MapFilterToggle(
                showReportsOnly = showReportsOnly,
                onToggle = { viewModel.toggleReportsOnly(it) },
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 24.dp)
            )
        }

        // Detailed View Bottom Sheet
        if (showBottomSheet && selectedRoad != null) {
            RoadDetailsSheet(
                road = selectedRoad!!,
                reportCount = allReports.count { it.roadId == selectedRoad!!.roadId && !it.isResolved },
                onDismiss = { showBottomSheet = false },
                onRoadClick = onRoadClick,
                onReportClick = onReportDamageClick
            )
        }
    }
}

@Composable
private fun MapLegendOverlay(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.width(160.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            LegendRow(color = Color(0xFF4CAF50), label = "Healthy (30-100)")
            LegendRow(color = Color(0xFFFF9800), label = "Warning (10-70)")
            LegendRow(color = Color(0xFFF44336), label = "Critical (<10)")
        }
    }
}

@Composable
private fun MapFilterToggle(showReportsOnly: Boolean, onToggle: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        shadowElevation = 8.dp
    ) {
        Row(modifier = Modifier.padding(6.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterButton(text = "All Roads", isSelected = !showReportsOnly, onClick = { onToggle(false) })
            FilterButton(text = "Reports Only", isSelected = showReportsOnly, onClick = { onToggle(true) })
        }
    }
}

@Composable
private fun FilterButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Text(text, fontSize = 13.sp)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoadDetailsSheet(
    road: Road,
    reportCount: Int,
    onDismiss: () -> Unit,
    onRoadClick: (Int) -> Unit,
    onReportClick: (Int) -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth().padding(bottom = 40.dp, start = 24.dp, end = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = road.roadName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(text = "${road.talukaName}, ${road.districtName}", color = Color.Gray)
            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                HealthScoreGauge(score = road.healthScore, modifier = Modifier.size(100.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = reportCount.toString(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text(text = "Active Reports", style = MaterialTheme.typography.labelMedium)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedButton(onClick = { onDismiss(); onRoadClick(road.roadId) }, modifier = Modifier.weight(1f)) { Text("Details") }
                Button(onClick = { onDismiss(); onReportClick(road.roadId) }, modifier = Modifier.weight(1f)) { Text("Report") }
            }
        }
    }
}

@Composable
private fun LegendRow(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(10.dp).background(color, CircleShape))
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = label, fontSize = 11.sp, color = Color.DarkGray)
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
