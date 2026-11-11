package com.example.foodapp.ui.settings

import android.app.Application
import android.content.pm.PackageInfo
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.AppUpdates
import com.example.foodapp.data.AvailableUpdate
import com.example.foodapp.data.SettingsStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

// Tutto ciò che la schermata Impostazioni deve mostrare: versione installata, preferenze,
// esito dell'ultimo controllo e stato del download.
data class SettingsUiState(
    val installedVersion: String = "",
    val installedCode: Long = 0L,
    val autoCheck: Boolean = true,
    val lastCheckAt: Long = 0L,
    val isChecking: Boolean = false,
    val update: AvailableUpdate? = null,
    val upToDate: Boolean = false,
    val isDownloading: Boolean = false,
    val downloadProgress: Int = 0,
    val apkFile: File? = null,
    val error: String? = null
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    // Preferenze persistenti: interruttore del controllo automatico e timestamp ultimo controllo.
    private val store = SettingsStore(application)

    // Stato interno modificabile; la UI riceve solo la versione in sola lettura.
    private val _state = MutableStateFlow(
        SettingsUiState(autoCheck = store.autoUpdateCheck, lastCheckAt = store.lastUpdateCheck)
    )
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    // Legge dal PackageManager la versione realmente installata sul dispositivo.
    init {
        val packageInfo = application.packageManager.getPackageInfo(application.packageName, 0)
        _state.update {
            it.copy(
                installedVersion = packageInfo.versionName.orEmpty(),
                installedCode = packageInfo.versionCodeCompat()
            )
        }
    }

    // Salva l'interruttore "Controlla automaticamente" e aggiorna subito la UI.
    fun setAutoCheck(enabled: Boolean) {
        store.autoUpdateCheck = enabled
        _state.update { it.copy(autoCheck = enabled) }
    }

    /** Controllo silenzioso all'avvio, al massimo una volta ogni 24 ore. */
    fun autoCheckIfDue() {
        // Se l'utente ha spento l'interruttore non parte nessuna richiesta di rete.
        if (!store.autoUpdateCheck) return
        // Timestamp salvato: se sono passate meno di 24 ore non si ricontrolla.
        if (System.currentTimeMillis() - store.lastUpdateCheck < CHECK_INTERVAL_MS) return
        // manual = false: in caso di errore non viene mostrato nulla all'utente.
        checkForUpdates(manual = false)
    }

    // Interroga GitHub Releases; manual = true se la richiesta parte dal pulsante "Controlla".
    fun checkForUpdates(manual: Boolean) {
        // Evita richieste doppie se un controllo o un download è già in corso.
        val current = _state.value
        if (current.isChecking || current.isDownloading) return
        // Azzera l'esito precedente e mostra lo spinner.
        _state.update {
            it.copy(isChecking = true, error = null, update = null, upToDate = false)
        }
        viewModelScope.launch {
            // Rete sempre su IO; runCatching trasforma le eccezioni in esito senza crash.
            runCatching { withContext(Dispatchers.IO) { AppUpdates.fetchLatest() } }
                .onSuccess { latest ->
                    val now = System.currentTimeMillis()
                    // Il timestamp si salva solo dopo una chiamata riuscita: un errore di rete
                    // non deve bloccare il prossimo controllo automatico per 24 ore.
                    store.lastUpdateCheck = now
                    // Confronto semver numerico: l'aggiornamento è proposto solo se più recente.
                    val newer = latest != null &&
                        AppUpdates.isNewer(latest.versionName, _state.value.installedVersion)
                    _state.update {
                        it.copy(
                            isChecking = false,
                            lastCheckAt = now,
                            update = if (newer) latest else null,
                            upToDate = !newer
                        )
                    }
                }
                .onFailure { error ->
                    // L'errore è visibile solo per un controllo manuale; quello automatico resta silenzioso.
                    _state.update {
                        it.copy(
                            isChecking = false,
                            error = if (manual) {
                                "Controllo non riuscito: ${error.message ?: "errore di rete"}"
                            } else {
                                it.error
                            }
                        )
                    }
                }
        }
    }

    // Scarica l'APK della release nella cache privata dell'app e ne segue il progresso.
    fun downloadUpdate() {
        val update = _state.value.update ?: return
        if (_state.value.isDownloading) return
        // Stato "download in corso": la UI mostra la barra di avanzamento.
        _state.update {
            it.copy(isDownloading = true, downloadProgress = 0, apkFile = null, error = null)
        }
        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    // Destinazione: cacheDir/updates/fastbite-<versione>.apk, la stessa cartella
                    // esposta dal FileProvider in res/xml/file_paths.xml.
                    val dir = File(getApplication<Application>().cacheDir, "updates")
                    val target = File(dir, "fastbite-${update.versionName}.apk")
                    AppUpdates.download(update, target) { percent ->
                        // Callback a ogni blocco letto: aggiorna la percentuale mostrata.
                        _state.update { it.copy(downloadProgress = percent) }
                    }
                    target
                }
            }
                .onSuccess { file ->
                    // apkFile valorizzato fa partire l'installer in SettingsScreen.
                    _state.update {
                        it.copy(isDownloading = false, downloadProgress = 100, apkFile = file)
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isDownloading = false,
                            error = "Download non riuscito: ${error.message ?: "errore di rete"}"
                        )
                    }
                }
        }
    }

    /** Evita di rilanciare l'installer a ogni rientro nella schermata. */
    fun consumeApkFile() {
        _state.update { it.copy(apkFile = null, downloadProgress = 0) }
    }

    // versionCode è deprecato da Android 9 (API 28): si usa longVersionCode quando disponibile.
    private fun PackageInfo.versionCodeCompat(): Long =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            longVersionCode
        } else {
            @Suppress("DEPRECATION")
            versionCode.toLong()
        }

    private companion object {
        // Intervallo minimo tra due controlli automatici: 24 ore in millisecondi.
        const val CHECK_INTERVAL_MS = 24L * 60L * 60L * 1000L
    }
}
