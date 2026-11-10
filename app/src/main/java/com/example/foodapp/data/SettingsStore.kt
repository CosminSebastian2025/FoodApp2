package com.example.foodapp.data

import android.content.Context

/**
 * Preferenze locali separate dalla sessione: restano su disco anche
 * quando l'app viene aggiornata (manualmente o da GitHub Releases).
 */
class SettingsStore(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("fastbite_settings", Context.MODE_PRIVATE)

    var autoUpdateCheck: Boolean
        get() = prefs.getBoolean(KEY_AUTO_UPDATE, true)
        set(value) {
            prefs.edit().putBoolean(KEY_AUTO_UPDATE, value).apply()
        }

    var lastUpdateCheck: Long
        get() = prefs.getLong(KEY_LAST_CHECK, 0L)
        set(value) {
            prefs.edit().putLong(KEY_LAST_CHECK, value).apply()
        }

    private companion object {
        const val KEY_AUTO_UPDATE = "autoUpdateCheck"
        const val KEY_LAST_CHECK = "lastUpdateCheck"
    }
}
