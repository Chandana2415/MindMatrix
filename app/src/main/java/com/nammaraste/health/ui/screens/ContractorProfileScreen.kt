package com.nammaraste.health.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nammaraste.health.ui.components.RoadHealthCard
import com.nammaraste.health.ui.components.StatCard
import com.nammaraste.health.ui.viewmodels.ContractorProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContractorProfileScreen(
    contractorName: String,
    onBack: () -> Unit,
    onRoadClick: (Int) -> Unit,
    viewModel: ContractorProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    
    LaunchedEffect(contractorName) {
        viewModel.setContractorName(contractorName)
    }

    val roads by viewModel.contractorRoads.collectAsState()
    val stats by viewModel.stats.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contractor Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                ContractorHeader(contractorName, stats.rating, onCallClick = {
                    Toast.makeText(context, "Calling $contractorName...", Toast.LENGTH_SHORT).show()
                })
            }

            item {
                Row(modifier = Modifier.padding(horizontal = 12.dp)) {
                    StatCard(
                        label = "Roads Built",
                        value = stats.roadsBuilt.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "Avg Score",
                        value = "${stats.avgHealthScore}%",
                        modifier = Modifier.weight(1f),
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                    StatCard(
                        label = "Active Warranties",
                        value = stats.activeWarranties.toString(),
                        modifier = Modifier.weight(1f),
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                }
            }

            item {
                Text(
                    text = "Assigned Roads",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(16.dp)
                )
            }

            items(roads) { road ->
                RoadHealthCard(
                    road = road,
                    onClick = { onRoadClick(road.roadId) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun ContractorHeader(name: String, rating: Int, onCallClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = name.take(1).uppercase(),
                        style = MaterialTheme.typography.displaySmall,
                        color = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = name,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Class-I PWD Contractor",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            RatingStars(rating = rating)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onCallClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Phone, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Contact Contractor")
            }
        }
    }
}

@Composable
fun RatingStars(rating: Int) {
    Row {
        repeat(5) { index ->
            Icon(
                imageVector = if (index < rating) Icons.Default.Star else Icons.Default.StarOutline,
                contentDescription = null,
                tint = if (index < rating) Color(0xFFFFD700) else Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
