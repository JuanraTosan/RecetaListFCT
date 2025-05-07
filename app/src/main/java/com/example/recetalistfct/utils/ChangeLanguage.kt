package com.example.recetalistfct.utils

import android.content.Context
import android.content.res.Configuration
import java.util.Locale


fun changeLanguage(context: Context, languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)

    val config = Configuration(context.resources.configuration)
    config.setLocale(locale)

    context.resources.updateConfiguration(config, context.resources.displayMetrics)
}

fun saveLanguagePreference(context: Context, languageCode: String) {
    val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    prefs.edit().putString("language", languageCode).apply()
}

fun getSavedLanguage(context: Context): String {
    val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    return prefs.getString("language", "es") ?: "es"
}

