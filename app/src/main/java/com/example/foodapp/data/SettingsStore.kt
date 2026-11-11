package com.example.foodapp.data

import android.content.Context

/**
 * Preferenze locali separate dalla sessione: restano su disco anche
 * quando l'app viene aggiornata (manualmente o da GitHub Releases).
 * SharedPreferences è legato al pacchetto, non alla versione: per questo
 * l'interruttore dell'updater non si perde con un update.
 */
class SettingsStore(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("fastbite_settings", Context.MODE_PRIVATE)

    /** Interruttore "Controlla automaticamente": decide se il controllo parte all'avvio. */
    var autoUpdateCheck: Boolean
        get() = prefs.getBoolean(KEY_AUTO_UPDATE, true)
        set(value) {
            prefs.edit().putBoolean(KEY_AUTO_UPDATE, value).apply()
        }

    /** Timestamp dell'ultimo controllo andato a buon fine (0 = mai): limita il controllo a una volta al giorno. */
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
