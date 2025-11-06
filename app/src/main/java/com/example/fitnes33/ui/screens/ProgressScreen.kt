package com.example.fitnes33.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitnes33.data.model.ActivityType
import com.example.fitnes33.ui.theme.*
import com.example.fitnes33.viewmodel.ProgressViewModel
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ProgressScreen(
    viewModel: ProgressViewModel
) {
    val state by viewModel.state.collectAsState()
    val targetMinutes = 480L // 8 horas objetivo
    
    LaunchedEffect(Unit) {
        viewModel.loadProgress()
    }
    val progress = if (targetMinutes > 0) {
        (state.totalMinutes.toFloat() / targetMinutes.toFloat()).coerceIn(0f, 1f)
    } else 0f
    
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000),
        label = "progress"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Título
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFFFFF) // Blanco
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Progreso del Día",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333) // Gris oscuro
                    )
                    Text(
                        text = state.currentDate,
                        fontSize = 14.sp,
                        color = Color(0xFF333333).copy(alpha = 0.7f)
                    )
                }
            }
            
            // Gráfico circular con colores por actividad
            Card(
                modifier = Modifier
                    .size(280.dp)
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFFFFF) // Blanco
                ),
                shape = RoundedCornerShape(140.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Gráfico personalizado con colores por actividad
                    val activities = remember(state) {
                        listOf(
                            Pair(state.transportMinutes, TransportColor),
                            Pair(state.studyMinutes, StudyColor),
                            Pair(state.walkingMinutes, WalkingColor),
                            Pair(state.sportMinutes, SportColor)
                        ).filter { it.first > 0 } // Solo actividades con minutos
                    }
                    
                    val totalProgress = remember(state) { 
                        (state.totalMinutes.toFloat() / targetMinutes.toFloat()).coerceIn(0f, 1f)
                    }
                    
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val size = this.size.minDimension
                        val strokeWidth = 20.dp.toPx()
                        val radius = (size - strokeWidth) / 2
                        val center = Offset(size / 2, size / 2)
                        
                        // Dibujar track de fondo (gris claro) para el objetivo completo
                        drawCircle(
                            color = Color(0xFFE0E0E0),
                            radius = radius,
                            center = center,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                        
                        if (activities.isNotEmpty() && state.totalMinutes > 0) {
                            val totalMinutes = state.totalMinutes.toFloat()
                            var currentAngle = -90f // Comenzar desde arriba
                            
                            // Dibujar cada segmento de actividad proporcional a su contribución al total
                            activities.forEach { (minutes, color) ->
                                val proportion = (minutes.toFloat() / totalMinutes).coerceIn(0f, 1f)
                                // El sweepAngle es proporcional al progreso total animado
                                val sweepAngle = proportion * 360f * animatedProgress * totalProgress
                                
                                if (sweepAngle > 0.1f) { // Solo dibujar si el ángulo es significativo
                                    drawArc(
                                        color = color,
                                        startAngle = currentAngle,
                                        sweepAngle = sweepAngle,
                                        useCenter = false,
                                        topLeft = Offset(
                                            center.x - radius,
                                            center.y - radius
                                        ),
                                        size = Size(radius * 2, radius * 2),
                                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                    )
                                    currentAngle += sweepAngle
                                }
                            }
                        }
                    }
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${(animatedProgress * 100).toInt()}%",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333) // Gris oscuro
                        )
                        Text(
                            text = "${state.totalMinutes} min",
                            fontSize = 20.sp,
                            color = Color(0xFF333333).copy(alpha = 0.7f)
                        )
                        Text(
                            text = "de ${targetMinutes} min",
                            fontSize = 14.sp,
                            color = Color(0xFF333333).copy(alpha = 0.6f)
                        )
                    }
                }
            }
            
            // Desglose por actividad
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActivityProgressItem(
                    icon = Icons.Default.DriveEta,
                    name = ActivityType.TRANSPORT.displayName,
                    minutes = state.transportMinutes,
                    color = TransportColor
                )
                ActivityProgressItem(
                    icon = Icons.Default.MenuBook,
                    name = ActivityType.STUDY.displayName,
                    minutes = state.studyMinutes,
                    color = StudyColor
                )
                ActivityProgressItem(
                    icon = Icons.Default.DirectionsWalk,
                    name = ActivityType.WALKING.displayName,
                    minutes = state.walkingMinutes,
                    color = WalkingColor
                )
                ActivityProgressItem(
                    icon = Icons.Default.Sports,
                    name = ActivityType.SPORT.displayName,
                    minutes = state.sportMinutes,
                    color = SportColor
                )
            }
        }
    }
}

@Composable
fun ActivityProgressItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    name: String,
    minutes: Long,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFFFFF) // Blanco
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = name,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF333333) // Gris oscuro
                )
            }
            Text(
                text = "${minutes} min",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

