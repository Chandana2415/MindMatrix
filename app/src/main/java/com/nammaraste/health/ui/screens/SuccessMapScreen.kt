package com.nammaraste.health.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nammaraste.health.data.local.entities.Road
import com.nammaraste.health.ui.components.SectionHeader
import com.nammaraste.health.ui.theme.healthColor
import com.nammaraste.health.ui.viewmodels.SuccessMapViewModel

@Composable
fun SuccessMapScreen(
    onRoadClick: (Int) -> Unit,
    viewModel: SuccessMapViewModel = hiltViewModel()
) {
    val sortedRoads by viewModel.sortedRoads.collectAsState()
    val topThree by viewModel.topThree.collectAsState()
    val needsAttention by viewModel.needsAttention.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Best Maintained Roads",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Dharwad & Gadag Talukas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (topThree.isNotEmpty()) {
            item {
                PodiumCard(topThree)
            }
        }

        item {
            SectionHeader(title = "Full Leaderboard")
        }

        itemsIndexed(sortedRoads) { index, road ->
            LeaderboardItem(rank = index + 1, road = road, onClick = { onRoadClick(road.roadId) })
        }

        item {
            SectionHeader(
                title = "Needs Immediate Attention",
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        itemsIndexed(needsAttention) { index, road ->
            AttentionCard(road = road, onClick = { onRoadClick(road.roadId) })
        }
    }
}

@Composable
fun PodiumCard(topThree: List<Road>) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(200.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // 2nd Place
            if (topThree.size >= 2) {
                PodiumColumn(road = topThree[1], rank = 2, height = 120.dp, color = Color(0xFFC0C0C0))
            }
            // 1st Place
            if (topThree.size >= 1) {
                PodiumColumn(road = topThree[0], rank = 1, height = 160.dp, color = Color(0xFFFFD700))
            }
            // 3rd Place
            if (topThree.size >= 3) {
                PodiumColumn(road = topThree[2], rank = 3, height = 100.dp, color = Color(0xFFCD7F32))
            }
        }
    }
}

@Composable
fun PodiumColumn(road: Road, rank: Int, height: Dp, color: Color) {
    val scale = remember { Animatable(0.8f) }
    
    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = 0.6f, stiffness = 100f)
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(100.dp).scale(scale.value)
    ) {
        Text(
            text = road.roadName,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = rank.toString(), fontWeight = FontWeight.Bold, color = Color.White)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(height),
            color = color.copy(alpha = 0.2f),
            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
        ) {
            Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.padding(8.dp)) {
                Text(
                    text = "${road.healthScore}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = color
                )
            }
        }
    }
}

@Composable
fun LeaderboardItem(rank: Int, road: Road, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "#$rank",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.width(40.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(text = road.roadName, style = MaterialTheme.typography.bodyMedium)
                LinearProgressIndicator(
                    progress = { road.healthScore / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .padding(top = 4.dp),
                    color = healthColor(road.healthScore),
                    trackColor = healthColor(road.healthScore).copy(alpha = 0.2f)
                )
            }
            Text(
                text = "${road.healthScore}",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = healthColor(road.healthScore),
                modifier = Modifier.padding(start = 12.dp)
            )
        }
    }
}

@Composable
fun AttentionCard(road: Road, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFC62828).copy(alpha = 0.05f)),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC62828).copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = Color(0xFFC62828)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = road.roadName,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Score: ${road.healthScore} | Critical condition",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFC62828)
                )
            }
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text("View Road", fontSize = 10.sp)
            }
        }
    }
}
