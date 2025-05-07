package com.example.recetalistfct.utils

import android.content.Context
import android.content.SharedPreferences


private const val PREFS_NAME = "app_preferences"
private const val KEY_DARK_MODE = "dark_mode"

fun saveThemePreference(context: Context, isDarkMode: Boolean) {
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit().putBoolean(KEY_DARK_MODE, isDarkMode).apply()
}

fun getSavedThemePreference(context: Context): Boolean {
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return prefs.getBoolean(KEY_DARK_MODE, false)
}