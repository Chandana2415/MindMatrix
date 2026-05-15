package com.nammaraste.health.ui.theme

import androidx.compose.ui.graphics.Color

val Primary = Color(0xFF1B6CA8)
val OnPrimary = Color(0xFFFFFFFF)
val PrimaryContainer = Color(0xFFD3E4FF)
val Secondary = Color(0xFF2E7D32)
val OnSecondary = Color(0xFFFFFFFF)
val SecondaryContainer = Color(0xFFB8F0BB)
val Error = Color(0xFFC62828)
val Background = Color(0xFFF5F7FA)
val Surface = Color(0xFFFFFFFF)
val TertiaryContainer = Color(0xFFFFF3CD)

fun healthColor(score: Int): Color = when {
    score >= 80 -> Color(0xFF2E7D32)
    score >= 50 -> Color(0xFFF57F17)
    else        -> Color(0xFFC62828)
}

fun healthLabel(score: Int): String = when {
    score >= 80 -> "Healthy"
    score >= 50 -> "Warning"
    else        -> "Critical"
}

fun warrantyStatus(endDate: Long): String {
    val now = System.currentTimeMillis()
    val days60 = 60L * 86400000
    return when {
        endDate < now         -> "Expired"
        endDate < now + days60 -> "Expiring Soon"
        else                   -> "Active"
    }
}

fun warrantyColor(status: String): Color = when(status) {
    "Active"        -> Color(0xFF2E7D32)
    "Expiring Soon" -> Color(0xFFF57F17)
    else            -> Color(0xFFC62828)
}
