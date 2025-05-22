package com.example.recetalistfct.utils

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

/**
 * Cambia el idioma de la aplicación en tiempo de ejecución.
 *
 * Aplica el idioma especificado a toda la aplicación, afectando al contexto proporcionado.
 *
 * @param context Contexto desde el cual se cambia el idioma. (normalmente Activity o Application)
 * @param languageCode Código del idioma en formato ISO 639-1 (ej: "es", "en").
 */
fun changeLanguage(context: Context, languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)

    val config = Configuration(context.resources.configuration)
    config.setLocale(locale)

    context.resources.updateConfiguration(config, context.resources.displayMetrics)
}

/**
 * Guarda la preferencia del idioma seleccionada por el usuario
 *
 * Esto permite recordar el idioma elegido incluso después de reiniciar la aplicación
 *
 * @param context Contexto usado para acceder a SharedPreferences.
 * @param languageCode Código del idioma formato ISO 639-1 (ej: "es", "en").
 */
fun saveLanguagePreference(context: Context, languageCode: String) {
    val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    prefs.edit().putString("language", languageCode).apply()
}

/**
 * Recupera el idioma guardado desde las preferencias de la aplicación.
 *
 * Si no hay un idioma guardado, devuelve "es" como valor predeterminado.
 *
 * @param context Contexto usado para acceder a SharedPreferences.
 * @return El código del idioma guardado (ej. "es", "en") o "es" si no hay uno definido.
 */
fun getSavedLanguage(context: Context): String {
    val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    return prefs.getString("language", "es") ?: "es"
}

