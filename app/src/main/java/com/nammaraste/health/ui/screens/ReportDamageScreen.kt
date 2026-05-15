package com.nammaraste.health.ui.screens

import android.Manifest
import android.os.Environment
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.nammaraste.health.R
import com.nammaraste.health.ui.components.IllustratedEmptyState
import com.nammaraste.health.ui.viewmodels.ReportViewModel
import com.nammaraste.health.util.LocationHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ReportDamageScreen(
    roadId: Int = -1,
    onBack: () -> Unit,
    viewModel: ReportViewModel = hiltViewModel()
) {
    val currentStep by viewModel.currentStep.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()
    val submitSuccess by viewModel.submitSuccess.collectAsState(initial = false)
    var showSuccessOverlay by remember { mutableStateOf(false) }

    LaunchedEffect(roadId) {
        if (roadId != -1) {
            viewModel.onRoadSelected(roadId)
        }
    }

    LaunchedEffect(submitSuccess) {
        if (submitSuccess) {
            showSuccessOverlay = true
            delay(2000)
            onBack()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Report Damage") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                StepIndicator(currentStep = currentStep)
                
                Spacer(modifier = Modifier.height(24.dp))

                Box(modifier = Modifier.weight(1f)) {
                    when (currentStep) {
                        1 -> PhotoStep(viewModel)
                        2 -> LocationStep(viewModel)
                        3 -> DetailsStep(viewModel)
                    }
                }

                if (isSubmitting) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
                }
            }
        }

        AnimatedVisibility(
            visible = showSuccessOverlay,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.success_check))
                    LottieAnimation(
                        composition = composition,
                        iterations = 1,
                        modifier = Modifier.size(200.dp)
                    )
                    Text(
                        text = "Report Submitted Successfully!",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun StepIndicator(currentStep: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(3) { index ->
            val step = index + 1
            val isActive = step == currentStep
            val isCompleted = step < currentStep
            
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isActive -> MaterialTheme.colorScheme.primary
                            isCompleted -> MaterialTheme.colorScheme.secondary
                            else -> Color.Transparent
                        }
                    )
                    .border(
                        width = 1.dp,
                        color = if (isActive || isCompleted) Color.Transparent else MaterialTheme.colorScheme.outline,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                } else {
                    Text(
                        text = step.toString(),
                        color = if (isActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
            if (index < 2) {
                HorizontalDivider(
                    modifier = Modifier.width(40.dp),
                    thickness = 2.dp,
                    color = if (isCompleted) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PhotoStep(viewModel: ReportViewModel) {
    val photoPath by viewModel.photoPath.collectAsState()
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    if (photoPath.isNotEmpty()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AsyncImage(
                model = photoPath,
                contentDescription = "Captured Photo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(onClick = { viewModel.onPhotoCaptured("") }) {
                Text("Retake Photo")
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { viewModel.nextStep() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Next Step")
            }
        }
    } else {
        if (cameraPermissionState.status.isGranted) {
            CameraPreview(onPhotoCaptured = { viewModel.onPhotoCaptured(it) })
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Camera permission is required to report damage.")
                Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                    Text("Grant Permission")
                }
            }
        }
    }
}

@Composable
fun CameraPreview(onPhotoCaptured: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val previewView = remember { PreviewView(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }

    LaunchedEffect(Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {}
        }, ContextCompat.getMainExecutor(context))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)))
        
        FloatingActionButton(
            onClick = {
                val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "damage_${System.currentTimeMillis()}.jpg")
                val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()
                imageCapture.takePicture(
                    outputOptions,
                    cameraExecutor,
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                            onPhotoCaptured(file.absolutePath)
                        }
                        override fun onError(exception: ImageCaptureException) {}
                    }
                )
            },
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp),
            containerColor = Color.White
        ) {
            Icon(Icons.Default.Camera, contentDescription = "Capture")
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LocationStep(viewModel: ReportViewModel) {
    val context = LocalContext.current
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val location by viewModel.location.collectAsState()
    val roads by viewModel.allRoads.collectAsState()
    val selectedRoadId by viewModel.selectedRoadId.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(locationPermissionState.status.isGranted) {
        if (locationPermissionState.status.isGranted && location == null) {
            scope.launch {
                LocationHelper.getLastLocation(context)?.let {
                    viewModel.onLocationDetected(it)
                }
            }
        }
    }

    Column {
        if (locationPermissionState.status.isGranted) {
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(location ?: LatLng(15.3173, 75.7139), 15f)
            }
            
            LaunchedEffect(location) {
                location?.let {
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 15f)
                }
            }

            Card(modifier = Modifier.fillMaxWidth().height(200.dp), shape = RoundedCornerShape(12.dp)) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState
                ) {
                    location?.let { Marker(state = MarkerState(position = it)) }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = location?.let { "Lat: ${"%.4f".format(it.latitude)}, Lng: ${"%.4f".format(it.longitude)}" } ?: "Detecting location...",
                    style = MaterialTheme.typography.bodyMedium
                )
                OutlinedButton(onClick = {
                    scope.launch {
                        LocationHelper.getLastLocation(context)?.let { viewModel.onLocationDetected(it) }
                    }
                }, contentPadding = PaddingValues(horizontal = 8.dp), modifier = Modifier.height(32.dp)) {
                    Text("Refresh", fontSize = 12.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Select Road Segment", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            var expanded by remember { mutableStateOf(false) }
            val selectedRoadName = roads.find { it.roadId == selectedRoadId }?.roadName ?: "Select Road"
            
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                OutlinedTextField(
                    value = selectedRoadName,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    roads.forEach { road ->
                        DropdownMenuItem(
                            text = { Text(road.roadName) },
                            onClick = {
                                viewModel.onRoadSelected(road.roadId)
                                expanded = false
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedButton(onClick = { viewModel.prevStep() }, modifier = Modifier.weight(1f)) {
                    Text("Back")
                }
                Button(
                    onClick = { viewModel.nextStep() },
                    modifier = Modifier.weight(1f),
                    enabled = location != null && selectedRoadId != -1
                ) {
                    Text("Next")
                }
            }
        } else {
            IllustratedEmptyState(
                type = "no_results",
                onCtaClick = { locationPermissionState.launchPermissionRequest() }
            )
        }
    }
}

@Composable
fun DetailsStep(viewModel: ReportViewModel) {
    val damageType by viewModel.damageType.collectAsState()
    val severity by viewModel.severity.collectAsState()
    val description by viewModel.description.collectAsState()
    val reporterName by viewModel.reporterName.collectAsState()

    val damageTypes = listOf(
        "Pothole" to Icons.Default.AddRoad,
        "Crack" to Icons.Default.Warning,
        "Waterlogging" to Icons.Default.WaterDrop,
        "Drain Clog" to Icons.Default.FilterAltOff,
        "Other" to Icons.Default.MoreHoriz
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Damage Type", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            items(damageTypes) { (type, icon) ->
                DamageTypeButton(
                    label = type,
                    icon = icon,
                    isSelected = damageType == type,
                    onClick = { viewModel.onDamageTypeSelected(type) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Severity", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 8.dp)) {
            listOf("Minor", "Moderate", "Severe").forEach { s ->
                FilterChip(
                    selected = severity == s,
                    onClick = { viewModel.onSeveritySelected(s) },
                    label = { Text(s) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { viewModel.onDescriptionChange(it) },
            label = { Text("Description (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = reporterName,
            onValueChange = { viewModel.onReporterNameChange(it) },
            label = { Text("Your Name (Required)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.weight(1f))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(onClick = { viewModel.prevStep() }, modifier = Modifier.weight(1f)) {
                Text("Back")
            }
            Button(
                onClick = { viewModel.submitReport() },
                modifier = Modifier.weight(1f),
                enabled = damageType.isNotEmpty() && severity.isNotEmpty() && reporterName.isNotEmpty()
            ) {
                Text("Submit Report")
            }
        }
    }
}

@Composable
fun DamageTypeButton(label: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.height(80.dp).border(1.dp, if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.primary
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}
