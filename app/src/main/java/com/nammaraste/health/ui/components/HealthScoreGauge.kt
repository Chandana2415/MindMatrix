package com.nammaraste.health.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nammaraste.health.ui.theme.Poppins
import com.nammaraste.health.ui.theme.healthColor
import com.nammaraste.health.ui.theme.healthLabel

@Composable
fun HealthScoreGauge(score: Int, modifier: Modifier = Modifier) {
    val sweepAngle = (score / 100f) * 270f
    val animatedSweepAngle by animateFloatAsState(
        targetValue = sweepAngle,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 100f),
        label = "gauge"
    )

    val color = healthColor(score)
    val label = healthLabel(score)

    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        Canvas(modifier = Modifier.matchParentSize().padding(8.dp)) {
            // Background Track Arc
            drawArc(
                color = color.copy(alpha = 0.1f),
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = size.width * 0.08f, cap = StrokeCap.Round)
            )
            // Animated Indicator Arc
            drawArc(
                color = color,
                startAngle = 135f,
                sweepAngle = animatedSweepAngle,
                useCenter = false,
                style = Stroke(width = size.width * 0.08f, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = score.toString(),
                style = MaterialTheme.typography.displaySmall.copy(
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = (modifier.toString().filter { it.isDigit() }.take(2).toIntOrNull()?.let { it / 4 } ?: 24).sp
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
