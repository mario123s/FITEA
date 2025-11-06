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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitnes33.data.model.ActivityType
import com.example.fitnes33.data.model.TimeRecord
import com.example.fitnes33.data.repository.TimeRepository
import com.example.fitnes33.ui.theme.*
import com.example.fitnes33.util.formatDuration
import kotlinx.coroutines.flow.first

@Composable
fun StatisticsScreen(
    repository: TimeRepository
) {
    // Usar un estado local para almacenar los registros
    var recordsList by remember { mutableStateOf<List<TimeRecord>>(emptyList()) }
    
    LaunchedEffect(Unit) {
        // Obtener registros de los últimos 30 días
        val currentDate = repository.getCurrentDate()
        val dates = (0..29).map { days ->
            val cal = java.util.Calendar.getInstance()
            cal.add(java.util.Calendar.DAY_OF_YEAR, -days)
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            sdf.format(cal.time)
        }
        
        // Recopilar todos los registros de las fechas usando first() para obtener solo el valor actual
        val allRecords = mutableListOf<TimeRecord>()
        dates.forEach { date ->
            val records = repository.getRecordsByDate(date).first()
            allRecords.addAll(records)
        }
        recordsList = allRecords
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        FireFitBlue,
                        FireFitViolet
                    )
                )
            )
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
                    containerColor = FireFitDarkGray.copy(alpha = 0.9f)
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Estadísticas",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = FireFitWhite
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Historial de actividades",
                        fontSize = 14.sp,
                        color = FireFitLightGray
                    )
                }
            }
            
            // Lista de registros
            if (recordsList.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val groupedRecords = recordsList.groupBy { it.date }
                    items(groupedRecords.entries.toList()) { entry ->
                        DateGroupCard(entry.key, entry.value)
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay registros aún",
                        color = FireFitLightGray
                    )
                }
            }
        }
    }
}

@Composable
fun DateGroupCard(date: String, records: List<TimeRecord>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = FireFitDarkGray.copy(alpha = 0.9f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = date,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = FireFitOrange,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            records.forEach { record ->
                RecordItem(record)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun RecordItem(record: TimeRecord) {
    val icon = when (record.activityType) {
        ActivityType.TRANSPORT -> Icons.Default.DriveEta
        ActivityType.STUDY -> Icons.Default.MenuBook
        ActivityType.WALKING -> Icons.Default.DirectionsWalk
        ActivityType.SPORT -> Icons.Default.Sports
    }
    
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
                contentDescription = record.activityType.displayName,
                tint = FireFitOrange,
                modifier = Modifier.size(20.dp)
            )
            Column {
                Text(
                    text = record.activityType.displayName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = FireFitWhite
                )
                Text(
                    text = formatDuration(record.duration),
                    fontSize = 12.sp,
                    color = FireFitLightGray
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
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        FireFitBlue,
                        FireFitViolet
                    )
                )
            )
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
                    containerColor = FireFitDarkGray.copy(alpha = 0.9f)
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
                        color = FireFitWhite
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
                        containerColor = FireFitDarkGray.copy(alpha = 0.9f)
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
                            color = FireFitWhite,
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
                        containerColor = FireFitDarkGray.copy(alpha = 0.9f)
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
                            color = FireFitWhite,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "Mi Tiempo Activo v1.0",
                            fontSize = 14.sp,
                            color = FireFitLightGray
                        )
                        Text(
                            text = "Registra y gestiona tu tiempo activo diario",
                            fontSize = 12.sp,
                            color = FireFitGray,
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

