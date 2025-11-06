package com.example.fitnes33.data.repository

import com.example.fitnes33.data.dao.TimeRecordDao
import com.example.fitnes33.data.model.ActivityType
import com.example.fitnes33.data.model.TimeRecord
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

class TimeRepository(private val dao: TimeRecordDao) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    fun getCurrentDate(): String = dateFormat.format(Date())
    
    fun getRecordsByDate(date: String): Flow<List<TimeRecord>> = dao.getRecordsByDate(date)
    
    fun getRecordsByActivityAndDate(activityType: ActivityType, date: String): Flow<List<TimeRecord>> =
        dao.getRecordsByActivityAndDate(activityType, date)
    
    fun getActiveRecords(): Flow<List<TimeRecord>> = dao.getActiveRecords()
    
    suspend fun insertRecord(record: TimeRecord): Long = dao.insertRecord(record)
    
    suspend fun updateRecord(record: TimeRecord) = dao.updateRecord(record)
    
    suspend fun getTotalDurationByActivityAndDate(activityType: ActivityType, date: String): Long =
        dao.getTotalDurationByActivityAndDate(activityType, date) ?: 0L
    
    suspend fun getTotalDurationByDate(date: String): Long =
        dao.getTotalDurationByDate(date) ?: 0L
}

