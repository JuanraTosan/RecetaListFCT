package com.example.recetalistfct.session

import android.content.Context
import android.content.SharedPreferences

object UserSessionManager{
    private const val PREF_NAME = "user_session"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }


    fun updateUser(id: String) {
        prefs.edit()
            .putString(KEY_USER_ID, id)
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .apply()
    }

    val currentUserId: String?
        get() = prefs.getString(KEY_USER_ID, null)


    fun isUserLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}