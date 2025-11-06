package com.example.fitnes33.data.dao

import androidx.room.*
import com.example.fitnes33.data.model.ActivityType
import com.example.fitnes33.data.model.TimeRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface TimeRecordDao {
    @Query("SELECT * FROM time_records WHERE date = :date ORDER BY startTime DESC")
    fun getRecordsByDate(date: String): Flow<List<TimeRecord>>
    
    @Query("SELECT * FROM time_records WHERE activityType = :activityType AND date = :date")
    fun getRecordsByActivityAndDate(activityType: ActivityType, date: String): Flow<List<TimeRecord>>
    
    @Query("SELECT * FROM time_records WHERE endTime IS NULL")
    fun getActiveRecords(): Flow<List<TimeRecord>>
    
    @Query("SELECT * FROM time_records ORDER BY date DESC, startTime DESC")
    fun getAllRecords(): Flow<List<TimeRecord>>
    
    @Insert
    suspend fun insertRecord(record: TimeRecord): Long
    
    @Update
    suspend fun updateRecord(record: TimeRecord)
    
    @Query("SELECT SUM(duration) FROM time_records WHERE activityType = :activityType AND date = :date")
    suspend fun getTotalDurationByActivityAndDate(activityType: ActivityType, date: String): Long?
    
    @Query("SELECT SUM(duration) FROM time_records WHERE date = :date")
    suspend fun getTotalDurationByDate(date: String): Long?
}

