package com.example.foodapp.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.FileProvider
import java.io.File

/**
 * Passa l'APK scaricato al programma di installazione di Android.
 * Da Android 8 (O) l'utente deve autorizzare l'installazione da questa app:
 * il primo tentativo apre la pagina di sistema, i successivi installano direttamente.
 */
object ApkInstaller {

    /** True se Android permette già di installare da FastBite (o se la versione non lo richiede). */
    fun canInstall(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.O ||
            context.packageManager.canRequestPackageInstalls()

    /** Apre la pagina di sistema "Installa app sconosciute" per il pacchetto FastBite. */
    fun openPermissionSettings(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
            .setData(Uri.parse("package:${context.packageName}"))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    /** Consegna l'APK in cache all'installer tramite FileProvider (l'APK non è leggibile altrove). */
    fun install(context: Context, apkFile: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            apkFile
        )
        val intent = Intent(Intent.ACTION_VIEW)
            .setDataAndType(uri, "application/vnd.android.package-archive")
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    /** Fallback: apre la pagina delle release nel browser, senza installazione in-app. */
    fun openReleasePage(context: Context) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(AppUpdates.RELEASES_PAGE))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}
