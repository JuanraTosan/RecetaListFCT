package com.example.recetalistfct.session

import android.content.Context
import android.content.SharedPreferences

/**
 * Gestor de sesión del usuario.
 *
 * Este objeto singleton se encarga de:
 * - Iniciar y actualizar la sesión del usuario actual.
 * - Verificar si hay un usuario autenticado.
 * - Obtener el ID del usuario actual.
 * - Limpiar la sesión (por ejemplo, al cerrar sesión).
 *
 * Utiliza SharedPreferences para almacenar los datos localmente.
 */
object UserSessionManager{
    private const val PREF_NAME = "user_session"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"

    private lateinit var prefs: SharedPreferences

    /**
     * Inicializa el gestor de sesión.
     *
     * Debe llamarse una vez en el ciclo de vida de la aplicación,
     * preferiblemente en el método `onCreate` de la MainActivity.
     *
     * @param context Contexto de la aplicación.
     */
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Actualiza los datos del usuario en la sesión.
     *
     * Establece el ID del usuario y marca como activa la sesión.
     *
     * @param id ID único del usuario (Firebase Auth UID).
     */
    fun updateUser(id: String) {
        prefs.edit()
            .putString(KEY_USER_ID, id)
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .apply()
    }

    /**
     * Obtiene el ID del usuario actual.
     *
     * @return El ID del usuario logueado o `null` si no hay sesión activa.
     */
    val currentUserId: String?
        get() = prefs.getString(KEY_USER_ID, null)

    /**
     * Comprueba si hay un usuario autenticado.
     *
     * @return `true` si el usuario ha iniciado sesión; `false` en caso contrario.
     */
    fun isUserLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    /**
     * Limpia la sesión del usuario.
     *
     * Elimina todos los datos guardados relacionados con la sesión actual.
     */
    fun clearSession() {
        prefs.edit().clear().apply()
    }
}