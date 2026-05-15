package com.nammaraste.health.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Long.toRelativeTime(): String {
    val diff = System.currentTimeMillis() - this
    val minutes = diff / 60000
    val hours = diff / 3600000
    val days = diff / 86400000
    return when {
        minutes < 1  -> "Just now"
        minutes < 60 -> "$minutes minutes ago"
        hours < 24   -> "$hours hours ago"
        days < 7     -> "$days days ago"
        else         -> toFullDate()
    }
}

fun Long.toFullDate(): String {
    val sdf = SimpleDateFormat("d MMM yyyy, hh:mm a", Locale.getDefault())
    return sdf.format(Date(this))
}

fun Long.toDateOnly(): String {
    val sdf = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
    return sdf.format(Date(this))
}
