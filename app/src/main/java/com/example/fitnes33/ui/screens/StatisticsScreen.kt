package com.example.fitnes33.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitnes33.data.model.ActivityStatistics
import com.example.fitnes33.data.model.ActivityType
import com.example.fitnes33.data.repository.TimeRepository
import com.example.fitnes33.ui.theme.*
import com.example.fitnes33.util.formatDuration
import kotlinx.coroutines.launch


@Composable
fun StatisticsScreen(
    repository: TimeRepository
) {
    var statisticsList by remember { mutableStateOf<List<ActivityStatistics>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    
    fun loadStatistics() {
        scope.launch {
            isLoading = true
            // Obtener registros de los últimos 30 días
            val currentDate = repository.getCurrentDate()
            val cal = java.util.Calendar.getInstance()
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            
            // Calcular fecha de inicio (30 días atrás)
            cal.add(java.util.Calendar.DAY_OF_YEAR, -29)
            val startDate = sdf.format(cal.time)
            
            // Obtener estadísticas agrupadas
            val stats = repository.getGroupedStatisticsByDateRange(startDate, currentDate)
            statisticsList = stats
            isLoading = false
        }
    }
    
    LaunchedEffect(Unit) {
        loadStatistics()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Header con botón de reinicio
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Estadísticas",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Resumen de actividades",
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                }
                
                // Botón de reinicio
                Card(
                    modifier = Modifier
                        .size(48.dp)
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = BackgroundWhite
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    IconButton(
                        onClick = { loadStatistics() },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reiniciar estadísticas",
                            tint = TextDarkGray.copy(alpha = 0.8f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
            
            // Lista de estadísticas agrupadas
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = TransportColor)
                }
            } else if (statisticsList.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(statisticsList) { stat ->
                        ActivityStatisticsCard(stat)
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay registros aún",
                        fontSize = 16.sp,
                        color = TextDarkGray.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun ActivityStatisticsCard(statistics: ActivityStatistics) {
    val (icon, iconColor) = when (statistics.activityType) {
        ActivityType.TRANSPORT -> Icons.Default.DriveEta to TransportColor
        ActivityType.STUDY -> Icons.Default.MenuBook to StudyColor
        ActivityType.WALKING -> Icons.Default.DirectionsWalk to WalkingColor
        ActivityType.SPORT -> Icons.Default.Sports to SportColor
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundWhite
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícono a la izquierda
            Icon(
                imageVector = icon,
                contentDescription = statistics.activityType.displayName,
                tint = iconColor,
                modifier = Modifier.size(48.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Información a la derecha
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = statistics.activityType.displayName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDarkGray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatDuration(statistics.totalDuration),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextDarkGray.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun SettingsScreen(
    preferencesManager: com.example.fitnes33.util.PreferencesManager,
    onLogout: () -> Unit
) {
    var userName by remember { mutableStateOf(preferencesManager.getUserName()) }
    
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
            // Header
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
                        text = "Configuración",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333) // Gris oscuro
                    )
                }
            }
            
            // Opciones
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFFFFF) // Blanco
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Nombre de usuario",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF333333), // Gris oscuro
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = userName,
                            onValueChange = {
                                userName = it
                                preferencesManager.saveUserName(it)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FireFitOrange,
                                unfocusedBorderColor = FireFitGray,
                                focusedLabelColor = FireFitOrange
                            )
                        )
                    }
                }
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFFFFF) // Blanco
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Información de la app",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF333333), // Gris oscuro
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "Mi Tiempo Activo v1.0",
                            fontSize = 14.sp,
                            color = Color(0xFF333333).copy(alpha = 0.7f)
                        )
                        Text(
                            text = "Registra y gestiona tu tiempo activo diario",
                            fontSize = 12.sp,
                            color = Color(0xFF333333).copy(alpha = 0.6f),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                
                // Botón de cerrar sesión
                Button(
                    onClick = {
                        preferencesManager.saveUserLoggedIn(false)
                        onLogout()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FireFitCoral
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Cerrar Sesión",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

