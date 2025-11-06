package com.example.fitnes33.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitnes33.data.model.ActivityType
import com.example.fitnes33.ui.theme.*
import com.example.fitnes33.viewmodel.ProgressViewModel

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
            
            // Gráfico circular
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
                    CircularProgressIndicator(
                        progress = animatedProgress,
                        modifier = Modifier.fillMaxSize(),
                        strokeWidth = 20.dp,
                        color = FireFitOrange,
                        trackColor = Color(0xFFE0E0E0) // Gris claro para el track
                    )
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

