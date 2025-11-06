package com.example.fitnes33

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitnes33.data.database.AppDatabase
import com.example.fitnes33.data.repository.TimeRepository
import com.example.fitnes33.ui.screens.*
import com.example.fitnes33.ui.theme.Fitnes33Theme
import com.example.fitnes33.util.PreferencesManager
import com.example.fitnes33.viewmodel.ProgressViewModel
import com.example.fitnes33.viewmodel.TimeTrackingViewModel
import com.example.fitnes33.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val database = AppDatabase.getDatabase(this)
        val repository = TimeRepository(database.timeRecordDao())
        val preferencesManager = PreferencesManager(this)
        val viewModelFactory = ViewModelFactory(repository)
        
        setContent {
            Fitnes33Theme {
                MiTiempoActivoApp(
                    repository = repository,
                    preferencesManager = preferencesManager,
                    viewModelFactory = viewModelFactory
                )
            }
        }
    }
}

@Composable
fun MiTiempoActivoApp(
    repository: TimeRepository,
    preferencesManager: PreferencesManager,
    viewModelFactory: ViewModelFactory
) {
    var isLoggedIn by remember { mutableStateOf(preferencesManager.isUserLoggedIn()) }
    
    if (!isLoggedIn) {
        LoginScreen(
            onLoginSuccess = { isLoggedIn = true },
            preferencesManager = preferencesManager
        )
    } else {
        MainNavigation(
            repository = repository,
            preferencesManager = preferencesManager,
            viewModelFactory = viewModelFactory,
            onLogout = { isLoggedIn = false }
        )
    }
}

@Composable
fun MainNavigation(
    repository: TimeRepository,
    preferencesManager: PreferencesManager,
    viewModelFactory: ViewModelFactory,
    onLogout: () -> Unit
) {
    var currentDestination by remember { mutableStateOf(AppDestinations.HOME) }
    val userName = remember { preferencesManager.getUserName() }
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                AppDestinations.entries.forEach { destination ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                destination.icon,
                                contentDescription = destination.label
                            )
                        },
                        label = { Text(destination.label) },
                        selected = destination == currentDestination,
                        onClick = { currentDestination = destination }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentDestination) {
                AppDestinations.HOME -> {
                    val viewModel: TimeTrackingViewModel = viewModel(
                        factory = viewModelFactory
                    )
                    HomeScreen(
                        viewModel = viewModel,
                        userName = userName
                    )
                }
                AppDestinations.PROGRESS -> {
                    val viewModel: ProgressViewModel = viewModel(
                        factory = viewModelFactory
                    )
                    ProgressScreen(viewModel = viewModel)
                }
                AppDestinations.STATISTICS -> {
                    StatisticsScreen(repository = repository)
                }
                AppDestinations.SETTINGS -> {
                    SettingsScreen(
                        preferencesManager = preferencesManager,
                        onLogout = onLogout
                    )
                }
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    HOME("Inicio", Icons.Default.Home),
    PROGRESS("Progreso", Icons.Default.Favorite),
    STATISTICS("Estadísticas", Icons.Default.List),
    SETTINGS("Configuración", Icons.Default.Settings),
}
