package com.example.fitnes33.util

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = 
        context.getSharedPreferences("MiTiempoActivo_Prefs", Context.MODE_PRIVATE)
    
    fun saveUserLoggedIn(isLoggedIn: Boolean) {
        prefs.edit().putBoolean("is_logged_in", isLoggedIn).apply()
    }
    
    fun isUserLoggedIn(): Boolean {
        return prefs.getBoolean("is_logged_in", false)
    }
    
    fun saveUserName(name: String) {
        prefs.edit().putString("user_name", name).apply()
    }
    
    fun getUserName(): String {
        return prefs.getString("user_name", "Usuario") ?: "Usuario"
    }
}

