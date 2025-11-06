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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitnes33.data.model.ActivityType
import com.example.fitnes33.ui.theme.*
import com.example.fitnes33.util.formatDuration
import com.example.fitnes33.viewmodel.TimeTrackingViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    viewModel: TimeTrackingViewModel = viewModel(),
    userName: String
) {
    val state by viewModel.state.collectAsState()
    val dateFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("es", "ES"))
    val date = try {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(state.currentDate)
        dateFormat.format(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(state.currentDate)!!)
    } catch (e: Exception) {
        state.currentDate
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header con saludo y fecha
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFFFFF) // Blanco
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "¡Hola, $userName!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333) // Gris oscuro
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = date,
                        fontSize = 16.sp,
                        color = Color(0xFF333333).copy(alpha = 0.7f)
                    )
                }
            }
            
            // Lista de actividades
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActivityCard(
                    activityType = ActivityType.TRANSPORT,
                    icon = Icons.Default.DriveEta,
                    activityState = state.transport,
                    onStart = { viewModel.startActivity(ActivityType.TRANSPORT) },
                    onStop = { viewModel.stopActivity(ActivityType.TRANSPORT) }
                )
                
                ActivityCard(
                    activityType = ActivityType.STUDY,
                    icon = Icons.Default.MenuBook,
                    activityState = state.study,
                    onStart = { viewModel.startActivity(ActivityType.STUDY) },
                    onStop = { viewModel.stopActivity(ActivityType.STUDY) }
                )
                
                ActivityCard(
                    activityType = ActivityType.WALKING,
                    icon = Icons.Default.DirectionsWalk,
                    activityState = state.walking,
                    onStart = { viewModel.startActivity(ActivityType.WALKING) },
                    onStop = { viewModel.stopActivity(ActivityType.WALKING) }
                )
                
                ActivityCard(
                    activityType = ActivityType.SPORT,
                    icon = Icons.Default.Sports,
                    activityState = state.sport,
                    onStart = { viewModel.startActivity(ActivityType.SPORT) },
                    onStop = { viewModel.stopActivity(ActivityType.SPORT) }
                )
            }
        }
    }
}

@Composable
fun ActivityCard(
    activityType: ActivityType,
    icon: ImageVector,
    activityState: com.example.fitnes33.viewmodel.ActivityState,
    onStart: () -> Unit,
    onStop: () -> Unit
) {
    val animatedProgress by animateFloatAsState(
        targetValue = if (activityState.isActive) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "progress"
    )
    
    val activityColor = when (activityType) {
        ActivityType.TRANSPORT -> TransportColor
        ActivityType.STUDY -> StudyColor
        ActivityType.WALKING -> WalkingColor
        ActivityType.SPORT -> SportColor
    }
    
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
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = activityType.displayName,
                        tint = activityColor,
                        modifier = Modifier.size(32.dp)
                    )
                    Column {
                        Text(
                            text = activityType.displayName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333) // Gris oscuro
                        )
                        Text(
                            text = formatDuration(activityState.totalDurationToday + activityState.currentDuration),
                            fontSize = 14.sp,
                            color = Color(0xFF333333).copy(alpha = 0.7f)
                        )
                    }
                }
                
                // Botones Iniciar/Detener
                if (activityState.isActive) {
                    Button(
                        onClick = onStop,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FireFitCoral
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Detener")
                    }
                } else {
                    Button(
                        onClick = onStart,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = activityColor
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Iniciar")
                    }
                }
            }
            
            // Indicador de tiempo activo
            if (activityState.isActive) {
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    progress = animatedProgress,
                    color = activityColor,
                    trackColor = Color(0xFFE0E0E0) // Gris claro para el track
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tiempo activo: ${formatDuration(activityState.currentDuration)}",
                    fontSize = 12.sp,
                    color = activityColor
                )
            }
        }
    }
}


