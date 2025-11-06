package com.example.fitnes33.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnes33.data.repository.TimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class ProgressState(
    val totalMinutes: Long = 0L,
    val transportMinutes: Long = 0L,
    val studyMinutes: Long = 0L,
    val walkingMinutes: Long = 0L,
    val sportMinutes: Long = 0L,
    val currentDate: String = "",
    val isLoading: Boolean = false
)

class ProgressViewModel(private val repository: TimeRepository) : ViewModel() {
    private val _state = MutableStateFlow(ProgressState())
    val state: StateFlow<ProgressState> = _state.asStateFlow()
    
    init {
        loadProgress()
    }
    
    fun loadProgress(date: String? = null) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            
            val targetDate = date ?: repository.getCurrentDate()
            
            val transport = repository.getTotalDurationByActivityAndDate(
                com.example.fitnes33.data.model.ActivityType.TRANSPORT, 
                targetDate
            ) / 60000 // Convertir a minutos
            val study = repository.getTotalDurationByActivityAndDate(
                com.example.fitnes33.data.model.ActivityType.STUDY, 
                targetDate
            ) / 60000
            val walking = repository.getTotalDurationByActivityAndDate(
                com.example.fitnes33.data.model.ActivityType.WALKING, 
                targetDate
            ) / 60000
            val sport = repository.getTotalDurationByActivityAndDate(
                com.example.fitnes33.data.model.ActivityType.SPORT, 
                targetDate
            ) / 60000
            
            val total = transport + study + walking + sport
            
            _state.value = _state.value.copy(
                transportMinutes = transport,
                studyMinutes = study,
                walkingMinutes = walking,
                sportMinutes = sport,
                totalMinutes = total,
                currentDate = targetDate,
                isLoading = false
            )
        }
    }
}

