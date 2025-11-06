package com.example.fitnes33.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnes33.data.model.ActivityType
import com.example.fitnes33.data.model.TimeRecord
import com.example.fitnes33.data.repository.TimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

data class ActivityState(
    val isActive: Boolean = false,
    val startTime: Long? = null,
    val currentDuration: Long = 0L,
    val totalDurationToday: Long = 0L
)

data class TimeTrackingState(
    val transport: ActivityState = ActivityState(),
    val study: ActivityState = ActivityState(),
    val walking: ActivityState = ActivityState(),
    val sport: ActivityState = ActivityState(),
    val currentDate: String = "",
    val isLoading: Boolean = false
)

class TimeTrackingViewModel(private val repository: TimeRepository) : ViewModel() {
    private val _state = MutableStateFlow(TimeTrackingState())
    val state: StateFlow<TimeTrackingState> = _state.asStateFlow()
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    init {
        loadCurrentDate()
        loadTodayRecords()
        startTimer()
    }
    
    private fun loadCurrentDate() {
        val currentDate = repository.getCurrentDate()
        _state.value = _state.value.copy(currentDate = currentDate)
    }
    
    private fun loadTodayRecords() {
        viewModelScope.launch {
            val date = _state.value.currentDate
            if (date.isEmpty()) return@launch
            
            val transport = repository.getTotalDurationByActivityAndDate(ActivityType.TRANSPORT, date)
            val study = repository.getTotalDurationByActivityAndDate(ActivityType.STUDY, date)
            val walking = repository.getTotalDurationByActivityAndDate(ActivityType.WALKING, date)
            val sport = repository.getTotalDurationByActivityAndDate(ActivityType.SPORT, date)
            
            _state.value = _state.value.copy(
                transport = _state.value.transport.copy(totalDurationToday = transport),
                study = _state.value.study.copy(totalDurationToday = study),
                walking = _state.value.walking.copy(totalDurationToday = walking),
                sport = _state.value.sport.copy(totalDurationToday = sport)
            )
            
            // Cargar registros activos
            val activeRecords = repository.getActiveRecords().first()
            activeRecords.forEach { record ->
                when (record.activityType) {
                    ActivityType.TRANSPORT -> {
                        _state.value = _state.value.copy(
                            transport = _state.value.transport.copy(
                                isActive = true,
                                startTime = record.startTime
                            )
                        )
                    }
                    ActivityType.STUDY -> {
                        _state.value = _state.value.copy(
                            study = _state.value.study.copy(
                                isActive = true,
                                startTime = record.startTime
                            )
                        )
                    }
                    ActivityType.WALKING -> {
                        _state.value = _state.value.copy(
                            walking = _state.value.walking.copy(
                                isActive = true,
                                startTime = record.startTime
                            )
                        )
                    }
                    ActivityType.SPORT -> {
                        _state.value = _state.value.copy(
                            sport = _state.value.sport.copy(
                                isActive = true,
                                startTime = record.startTime
                            )
                        )
                    }
                }
            }
            
            // Observar cambios en registros activos
            viewModelScope.launch {
                repository.getActiveRecords().collect { activeRecords ->
                    // Resetear todos los estados activos primero
                    val transportActive = activeRecords.any { it.activityType == ActivityType.TRANSPORT }
                    val studyActive = activeRecords.any { it.activityType == ActivityType.STUDY }
                    val walkingActive = activeRecords.any { it.activityType == ActivityType.WALKING }
                    val sportActive = activeRecords.any { it.activityType == ActivityType.SPORT }
                    
                    activeRecords.forEach { record ->
                        when (record.activityType) {
                            ActivityType.TRANSPORT -> {
                                _state.value = _state.value.copy(
                                    transport = _state.value.transport.copy(
                                        isActive = true,
                                        startTime = record.startTime
                                    )
                                )
                            }
                            ActivityType.STUDY -> {
                                _state.value = _state.value.copy(
                                    study = _state.value.study.copy(
                                        isActive = true,
                                        startTime = record.startTime
                                    )
                                )
                            }
                            ActivityType.WALKING -> {
                                _state.value = _state.value.copy(
                                    walking = _state.value.walking.copy(
                                        isActive = true,
                                        startTime = record.startTime
                                    )
                                )
                            }
                            ActivityType.SPORT -> {
                                _state.value = _state.value.copy(
                                    sport = _state.value.sport.copy(
                                        isActive = true,
                                        startTime = record.startTime
                                    )
                                )
                            }
                        }
                    }
                    
                    // Si no hay registros activos para una actividad, desactivarla
                    if (!transportActive) {
                        _state.value = _state.value.copy(
                            transport = _state.value.transport.copy(
                                isActive = false,
                                startTime = null
                            )
                        )
                    }
                    if (!studyActive) {
                        _state.value = _state.value.copy(
                            study = _state.value.study.copy(
                                isActive = false,
                                startTime = null
                            )
                        )
                    }
                    if (!walkingActive) {
                        _state.value = _state.value.copy(
                            walking = _state.value.walking.copy(
                                isActive = false,
                                startTime = null
                            )
                        )
                    }
                    if (!sportActive) {
                        _state.value = _state.value.copy(
                            sport = _state.value.sport.copy(
                                isActive = false,
                                startTime = null
                            )
                        )
                    }
                }
            }
        }
    }
    
    fun startActivity(activityType: ActivityType) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val date = _state.value.currentDate
            
            val record = TimeRecord(
                activityType = activityType,
                startTime = now,
                endTime = null,
                duration = 0L,
                date = date
            )
            
            val recordId = repository.insertRecord(record)
            
            when (activityType) {
                ActivityType.TRANSPORT -> {
                    _state.value = _state.value.copy(
                        transport = _state.value.transport.copy(
                            isActive = true,
                            startTime = now
                        )
                    )
                }
                ActivityType.STUDY -> {
                    _state.value = _state.value.copy(
                        study = _state.value.study.copy(
                            isActive = true,
                            startTime = now
                        )
                    )
                }
                ActivityType.WALKING -> {
                    _state.value = _state.value.copy(
                        walking = _state.value.walking.copy(
                            isActive = true,
                            startTime = now
                        )
                    )
                }
                ActivityType.SPORT -> {
                    _state.value = _state.value.copy(
                        sport = _state.value.sport.copy(
                            isActive = true,
                            startTime = now
                        )
                    )
                }
            }
        }
    }
    
    fun stopActivity(activityType: ActivityType) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val date = _state.value.currentDate
            
            var startTime: Long? = null
            when (activityType) {
                ActivityType.TRANSPORT -> startTime = _state.value.transport.startTime
                ActivityType.STUDY -> startTime = _state.value.study.startTime
                ActivityType.WALKING -> startTime = _state.value.walking.startTime
                ActivityType.SPORT -> startTime = _state.value.sport.startTime
            }
            
            if (startTime == null) return@launch
            
            val duration = now - startTime
            
            // Buscar el registro activo y actualizarlo
            val activeRecords = repository.getActiveRecords().first()
            val activeRecord = activeRecords.find { it.activityType == activityType }
            if (activeRecord != null) {
                val updatedRecord = activeRecord.copy(
                    endTime = now,
                    duration = duration
                )
                repository.updateRecord(updatedRecord)
            }
            
            // Recargar totales después de actualizar
            val currentDate = _state.value.currentDate
            val transport = repository.getTotalDurationByActivityAndDate(ActivityType.TRANSPORT, currentDate)
            val study = repository.getTotalDurationByActivityAndDate(ActivityType.STUDY, currentDate)
            val walking = repository.getTotalDurationByActivityAndDate(ActivityType.WALKING, currentDate)
            val sport = repository.getTotalDurationByActivityAndDate(ActivityType.SPORT, currentDate)
            
            // Actualizar estado con los totales recargados
            when (activityType) {
                ActivityType.TRANSPORT -> {
                    _state.value = _state.value.copy(
                        transport = _state.value.transport.copy(
                            isActive = false,
                            startTime = null,
                            currentDuration = 0L,
                            totalDurationToday = transport
                        )
                    )
                }
                ActivityType.STUDY -> {
                    _state.value = _state.value.copy(
                        study = _state.value.study.copy(
                            isActive = false,
                            startTime = null,
                            currentDuration = 0L,
                            totalDurationToday = study
                        )
                    )
                }
                ActivityType.WALKING -> {
                    _state.value = _state.value.copy(
                        walking = _state.value.walking.copy(
                            isActive = false,
                            startTime = null,
                            currentDuration = 0L,
                            totalDurationToday = walking
                        )
                    )
                }
                ActivityType.SPORT -> {
                    _state.value = _state.value.copy(
                        sport = _state.value.sport.copy(
                            isActive = false,
                            startTime = null,
                            currentDuration = 0L,
                            totalDurationToday = sport
                        )
                    )
                }
            }
        }
    }
    
    private fun startTimer() {
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(1000) // Esperar 1 segundo
                val now = System.currentTimeMillis()
                
                val transportStartTime = _state.value.transport.startTime
                val transport = if (_state.value.transport.isActive && transportStartTime != null) {
                    _state.value.transport.copy(currentDuration = now - transportStartTime)
                } else {
                    _state.value.transport.copy(currentDuration = 0L)
                }
                
                val studyStartTime = _state.value.study.startTime
                val study = if (_state.value.study.isActive && studyStartTime != null) {
                    _state.value.study.copy(currentDuration = now - studyStartTime)
                } else {
                    _state.value.study.copy(currentDuration = 0L)
                }
                
                val walkingStartTime = _state.value.walking.startTime
                val walking = if (_state.value.walking.isActive && walkingStartTime != null) {
                    _state.value.walking.copy(currentDuration = now - walkingStartTime)
                } else {
                    _state.value.walking.copy(currentDuration = 0L)
                }
                
                val sportStartTime = _state.value.sport.startTime
                val sport = if (_state.value.sport.isActive && sportStartTime != null) {
                    _state.value.sport.copy(currentDuration = now - sportStartTime)
                } else {
                    _state.value.sport.copy(currentDuration = 0L)
                }
                
                _state.value = _state.value.copy(
                    transport = transport,
                    study = study,
                    walking = walking,
                    sport = sport
                )
            }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
    }
}

