package com.nammaraste.health.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SeverityBadge(severity: String) {
    val (bgColor, textColor) = when (severity) {
        "Severe" -> Color(0xFFC62828).copy(alpha = 0.1f) to Color(0xFFC62828)
        "Moderate" -> Color(0xFFF57F17).copy(alpha = 0.1f) to Color(0xFFF57F17)
        "Minor" -> Color(0xFF1B6CA8).copy(alpha = 0.1f) to Color(0xFF1B6CA8)
        else -> Color.Gray.copy(alpha = 0.1f) to Color.Gray
    }

    Text(
        text = severity,
        color = textColor,
        style = MaterialTheme.typography.labelLarge.copy(fontSize = 10.sp),
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    )
}
