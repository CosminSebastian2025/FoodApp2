package com.example.foodapp.data

import android.content.Context

/**
 * Salva la sessione su disco: senza questo il login viene perso a ogni
 * chiusura dell'app (lo stato in memoria muore con il processo).
 */
class SessionStore(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("fastbite_session", Context.MODE_PRIVATE)

    fun userId(): String = prefs.getString(KEY_USER_ID, null).orEmpty()
    fun name(): String = prefs.getString(KEY_NAME, null).orEmpty()
    fun email(): String = prefs.getString(KEY_EMAIL, null).orEmpty()

    fun save(userId: String, name: String, email: String) {
        prefs.edit()
            .putString(KEY_USER_ID, userId)
            .putString(KEY_NAME, name)
            .putString(KEY_EMAIL, email)
            .apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    private companion object {
        const val KEY_USER_ID = "userId"
        const val KEY_NAME = "name"
        const val KEY_EMAIL = "email"
    }
}
