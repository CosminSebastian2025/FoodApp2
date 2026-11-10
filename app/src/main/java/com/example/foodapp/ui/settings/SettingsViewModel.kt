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

    private val store = SettingsStore(application)

    private val _state = MutableStateFlow(
        SettingsUiState(autoCheck = store.autoUpdateCheck, lastCheckAt = store.lastUpdateCheck)
    )
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        val packageInfo = application.packageManager.getPackageInfo(application.packageName, 0)
        _state.update {
            it.copy(
                installedVersion = packageInfo.versionName.orEmpty(),
                installedCode = packageInfo.versionCodeCompat()
            )
        }
    }

    fun setAutoCheck(enabled: Boolean) {
        store.autoUpdateCheck = enabled
        _state.update { it.copy(autoCheck = enabled) }
    }

    /** Controllo silenzioso all'avvio, al massimo una volta ogni 24 ore. */
    fun autoCheckIfDue() {
        if (!store.autoUpdateCheck) return
        if (System.currentTimeMillis() - store.lastUpdateCheck < CHECK_INTERVAL_MS) return
        checkForUpdates(manual = false)
    }

    fun checkForUpdates(manual: Boolean) {
        val current = _state.value
        if (current.isChecking || current.isDownloading) return
        _state.update {
            it.copy(isChecking = true, error = null, update = null, upToDate = false)
        }
        viewModelScope.launch {
            runCatching { withContext(Dispatchers.IO) { AppUpdates.fetchLatest() } }
                .onSuccess { latest ->
                    val now = System.currentTimeMillis()
                    store.lastUpdateCheck = now
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

    fun downloadUpdate() {
        val update = _state.value.update ?: return
        if (_state.value.isDownloading) return
        _state.update {
            it.copy(isDownloading = true, downloadProgress = 0, apkFile = null, error = null)
        }
        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    val dir = File(getApplication<Application>().cacheDir, "updates")
                    val target = File(dir, "fastbite-${update.versionName}.apk")
                    AppUpdates.download(update, target) { percent ->
                        _state.update { it.copy(downloadProgress = percent) }
                    }
                    target
                }
            }
                .onSuccess { file ->
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

    private fun PackageInfo.versionCodeCompat(): Long =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            longVersionCode
        } else {
            @Suppress("DEPRECATION")
            versionCode.toLong()
        }

    private companion object {
        const val CHECK_INTERVAL_MS = 24L * 60L * 60L * 1000L
    }
}
