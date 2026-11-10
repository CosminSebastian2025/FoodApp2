package com.example.foodapp.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodapp.AppState
import com.example.foodapp.Brand
import com.example.foodapp.Emerald
import com.example.foodapp.Slate400
import com.example.foodapp.Slate500
import com.example.foodapp.Slate700
import com.example.foodapp.data.ApkInstaller
import com.example.foodapp.ui.components.BackHeader
import com.example.foodapp.ui.components.ErrorBanner
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SettingsScreen(state: AppState, viewModel: SettingsViewModel = viewModel()) {
    val ui by viewModel.state.collectAsState()
    val context = LocalContext.current

    // L'installer parte solo quando il download è completato.
    LaunchedEffect(ui.apkFile) {
        val file = ui.apkFile ?: return@LaunchedEffect
        if (ApkInstaller.canInstall(context)) {
            ApkInstaller.install(context, file)
        } else {
            ApkInstaller.openPermissionSettings(context)
        }
        viewModel.consumeApkFile()
    }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            BackHeader("Impostazioni") { state.screen = "profile" }
        }
        item {
            AppInfoCard(ui)
        }
        item {
            UpdateCard(ui, viewModel)
        }
        item {
            AutoUpdateCard(ui.autoCheck, viewModel::setAutoCheck)
        }
        item {
            Text(
                "Gli aggiornamenti vengono scaricati da GitHub Releases. Il controllo automatico " +
                    "avviene all'avvio, al massimo una volta al giorno.",
                fontSize = 10.sp,
                color = Slate400
            )
        }
    }
}

@Composable
private fun AppInfoCard(ui: SettingsUiState) {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Settings,
                null,
                tint = Brand,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("FastBite", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(
                    "Versione ${ui.installedVersion} (${ui.installedCode})",
                    fontSize = 11.sp,
                    color = Slate500
                )
                Text(
                    "Ultimo controllo: ${formatLastCheck(ui.lastCheckAt)}",
                    fontSize = 10.sp,
                    color = Slate400
                )
            }
        }
    }
}

@Composable
private fun UpdateCard(ui: SettingsUiState, viewModel: SettingsViewModel) {
    val context = LocalContext.current
    val update = ui.update

    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White)) {
        Column(
            Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.SystemUpdate,
                    null,
                    tint = Brand,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text("Aggiornamenti", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            when {
                ui.isChecking -> Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Brand,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(10.dp))
                    Text("Controllo aggiornamenti…", fontSize = 12.sp, color = Slate500)
                }

                update != null -> {
                    Text(
                        "Versione ${update.versionName} disponibile",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    if (update.notes.isNotBlank()) {
                        Text(
                            update.notes,
                            fontSize = 11.sp,
                            color = Slate500,
                            maxLines = 5,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (update.apkSize > 0) {
                        Text(
                            "APK ${formatSize(update.apkSize)}",
                            fontSize = 10.sp,
                            color = Slate400
                        )
                    }
                    if (ui.isDownloading) {
                        LinearProgressIndicator(
                            progress = { ui.downloadProgress / 100f },
                            modifier = Modifier.fillMaxWidth(),
                            color = Brand
                        )
                        Text(
                            "Download ${ui.downloadProgress}%",
                            fontSize = 10.sp,
                            color = Slate500
                        )
                    } else {
                        Button(
                            onClick = viewModel::downloadUpdate,
                            colors = ButtonDefaults.buttonColors(Brand),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Scarica e installa",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (!ApkInstaller.canInstall(context)) {
                            Text(
                                "Android chiederà di consentire l'installazione da FastBite.",
                                fontSize = 10.sp,
                                color = Slate400
                            )
                        }
                    }
                }

                ui.upToDate -> Text(
                    "✓ Hai già l'ultima versione",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Emerald
                )

                else -> Text(
                    "Controlla se è disponibile una nuova versione.",
                    fontSize = 12.sp,
                    color = Slate500
                )
            }

            ui.error?.let { message ->
                ErrorBanner(message = message, onRetry = { viewModel.checkForUpdates(true) })
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { viewModel.checkForUpdates(manual = true) },
                    enabled = !ui.isChecking && !ui.isDownloading,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Refresh, null, modifier = Modifier.size(15.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Controlla", fontSize = 12.sp, color = Slate700)
                }
                OutlinedButton(
                    onClick = { ApkInstaller.openReleasePage(context) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Pagina release", fontSize = 12.sp, color = Slate700)
                }
            }
        }
    }
}

@Composable
private fun AutoUpdateCard(autoCheck: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(Color.White)) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    "Controlla automaticamente",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Text(
                    "All'avvio, al massimo una volta al giorno",
                    fontSize = 10.sp,
                    color = Slate400
                )
            }
            Switch(
                checked = autoCheck,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(checkedTrackColor = Brand)
            )
        }
    }
}

/** Striscia mostrata sopra ogni schermata quando il controllo automatico trova un aggiornamento. */
@Composable
fun UpdateAvailableBanner(versionName: String, onOpen: () -> Unit) {
    Surface(color = Brand) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.SystemUpdate,
                null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "Nuova versione $versionName disponibile",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onOpen) {
                Text("Dettagli", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun formatLastCheck(millis: Long): String =
    if (millis <= 0L) "Mai"
    else SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ITALY).format(Date(millis))

private fun formatSize(bytes: Long): String =
    if (bytes <= 0L) "" else "%.1f MB".format(bytes / 1048576.0)
