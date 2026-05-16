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
    val context = LocalContext.current
    val allRoads by viewModel.allRoads.collectAsState()
    val allReports by viewModel.allReports.collectAsState()
    val showReportsOnly by viewModel.showReportsOnly.collectAsState()

    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val scope = rememberCoroutineScope()
    
    val sheetState = rememberModalBottomSheetState()
    var selectedRoad by remember { mutableStateOf<Road?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }

    val dharwadCenter = LatLng(15.4589, 75.0078)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(dharwadCenter, 11f)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.infrastructure_map), fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = {
                        viewModel.forceSeedData()
                        Toast.makeText(context, "Refreshing data...", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh Data")
                    }
                    IconButton(onClick = {
                        scope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(dharwadCenter, 11f)
                            )
                        }
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
                properties = MapProperties(
                    isMyLocationEnabled = locationPermissionState.status.isGranted,
                    mapType = MapType.NORMAL
                ),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    myLocationButtonEnabled = true,
                    mapToolbarEnabled = true
                )
            ) {
                if (!showReportsOnly) {
                    allRoads.forEach { road ->
                        key(road.roadId) {
                            val points = remember(road.polylinePoints) { 
                                PolylineParser.parsePolyline(road.polylinePoints) 
                            }
                            if (points.isNotEmpty()) {
                                Polyline(
                                    points = points,
                                    color = healthColor(road.healthScore),
                                    width = 14f,
                                    clickable = true,
                                    onClick = {
                                        selectedRoad = road
                                        showBottomSheet = true
                                    }
                                )
                            }
                        }
                    }
                }

                allReports.filter { !it.isResolved }.forEach { report ->
                    key(report.reportId) {
                        Marker(
                            state = rememberMarkerState(position = LatLng(report.latitude, report.longitude)),
                            title = report.damageType,
                            snippet = "${report.severity} • ${report.reportTimestamp.toRelativeTime()}",
                            icon = severityMarkerIcon(report.severity)
                        )
                    }
                }
            }

            // Legend Overlay (Based on user reference)
            Card(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .width(160.dp),
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

            // Filter Toggle Buttons (Based on user reference)
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                tonalElevation = 8.dp,
                shadowElevation = 12.dp
            ) {
                Row(
                    modifier = Modifier.padding(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.toggleReportsOnly(false) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!showReportsOnly) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                            contentColor = if (!showReportsOnly) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text("All Roads", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                    Button(
                        onClick = { viewModel.toggleReportsOnly(true) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (showReportsOnly) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                            contentColor = if (showReportsOnly) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text("Reports Only", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
            
            // Helpful Diagnostic Info
            if (allRoads.isEmpty()) {
                Box(modifier = Modifier.align(Alignment.Center).padding(32.dp)) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Map is Blank?",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "1. Add your API Key in Manifest\n2. Click the Refresh icon above to load data.",
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        if (showBottomSheet && selectedRoad != null) {
            val road = selectedRoad!!
            val roadReports = allReports.count { it.roadId == road.roadId && !it.isResolved }
            
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface,
                dragHandle = { BottomSheetDefaults.DragHandle() }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 40.dp, start = 24.dp, end = 24.dp),
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
                                fontWeight = FontWeight.Bold,
                                color = if (roadReports > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
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
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("View Details")
                        }
                        Button(
                            onClick = {
                                showBottomSheet = false
                                onReportDamageClick(road.roadId)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
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
        Box(modifier = Modifier.size(10.dp).background(color, CircleShape))
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.DarkGray)
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
