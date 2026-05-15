package com.nammaraste.health.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nammaraste.health.ui.theme.warrantyColor

@Composable
fun WarrantyChip(endDate: Long) {
    val now = System.currentTimeMillis()
    val days60 = 60L * 24 * 60 * 60 * 1000
    val status = when {
        endDate < now -> "Expired"
        endDate < now + days60 -> "Expiring Soon"
        else -> "Active"
    }
    val color = warrantyColor(status)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(color, CircleShape)
        )
        Text(
            text = status,
            color = color,
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 10.sp),
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}
