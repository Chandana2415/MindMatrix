package com.nammaraste.health.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun IllustratedEmptyState(
    type: String,
    modifier: Modifier = Modifier,
    onCtaClick: (() -> Unit)? = null
) {
    val (icon, title, subtitle) = when (type) {
        "no_roads" -> Triple(
            Icons.Default.LocationOn,
            "No Roads Found",
            "There are no roads recorded in the database for this region."
        )
        "no_reports" -> Triple(
            Icons.Default.CheckCircle,
            "All Clear!",
            "No damage reports found. The roads seem to be in good condition."
        )
        "no_results" -> Triple(
            Icons.Default.Search,
            "No Results",
            "We couldn't find anything matching your search or filters."
        )
        else -> Triple(
            Icons.Default.Info,
            "Nothing Here",
            "There is no data to display at the moment."
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (onCtaClick != null) {
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onCtaClick) {
                Text("Retry / Add New")
            }
        }
    }
}
