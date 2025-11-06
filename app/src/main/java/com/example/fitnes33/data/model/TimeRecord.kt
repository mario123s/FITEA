package com.example.fitnes33.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.fitnes33.data.database.Converters

@Entity(tableName = "time_records")
data class TimeRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @TypeConverters(Converters::class)
    val activityType: ActivityType,
    val startTime: Long, // timestamp en milisegundos
    val endTime: Long?, // null si está activo
    val duration: Long, // duración en milisegundos
    val date: String // formato YYYY-MM-DD
)

