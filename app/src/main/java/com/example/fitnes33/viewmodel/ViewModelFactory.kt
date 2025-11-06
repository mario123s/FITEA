package com.example.fitnes33.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fitnes33.data.repository.TimeRepository

class ViewModelFactory(
    private val repository: TimeRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimeTrackingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TimeTrackingViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(ProgressViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProgressViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

