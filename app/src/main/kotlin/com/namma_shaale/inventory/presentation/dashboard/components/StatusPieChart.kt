package com.namma_shaale.inventory.presentation.dashboard.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun StatusPieChart(
    greenCount: Int,
    yellowCount: Int,
    redCount: Int,
    modifier: Modifier = Modifier
) {
    val total = greenCount + yellowCount + redCount
    if (total == 0) return

    val greenAngle = (greenCount.toFloat() / total) * 360f
    val yellowAngle = (yellowCount.toFloat() / total) * 360f
    val redAngle = (redCount.toFloat() / total) * 360f

    val animateSweep by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 1000),
        label = "Sweep"
    )

    Box(
        modifier = modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(160.dp)) {
            val strokeWidth = 20.dp.toPx()
            
            // Draw Green
            drawArc(
                color = Color(0xFF4CAF50),
                startAngle = -90f,
                sweepAngle = greenAngle * animateSweep,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            
            // Draw Yellow
            drawArc(
                color = Color(0xFFFFC107),
                startAngle = -90f + (greenAngle * animateSweep),
                sweepAngle = yellowAngle * animateSweep,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            
            // Draw Red
            drawArc(
                color = Color(0xFFF44336),
                startAngle = -90f + (greenAngle * animateSweep) + (yellowAngle * animateSweep),
                sweepAngle = redAngle * animateSweep,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${((greenCount.toFloat() / total) * 100).toInt()}%",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Healthy",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
