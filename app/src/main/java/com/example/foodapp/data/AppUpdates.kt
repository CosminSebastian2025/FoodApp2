package com.example.foodapp.data

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.concurrent.TimeUnit

/** Dati minimi estratti dalla release GitHub che servono per proporre e scaricare l'aggiornamento. */
data class AvailableUpdate(
    /** Versione remota senza la "v" del tag, es. "1.1.8". */
    val versionName: String,
    /** Note della release, mostrate nella schermata Impostazioni. */
    val notes: String,
    /** URL diretto dell'asset APK. */
    val apkUrl: String,
    /** Dimensione dichiarata dell'APK, usata per la barra di avanzamento. */
    val apkSize: Long
)

/**
 * Unico punto di accesso agli aggiornamenti: legge l'ultima release pubblica
 * del repository tramite l'API GitHub e scarica l'APK allegato.
 * Non usa Retrofit perché l'API GitHub sta su un host diverso dal backend PHP.
 */
object AppUpdates {

    /** Pagina web delle release, usata dal pulsante "Pagina release" come fallback. */
    const val RELEASES_PAGE = "https://github.com/CosminSebastian2025/FoodApp2/releases/latest"

    /** Endpoint pubblico "latest release": restituisce l'ultima release non draft e non prerelease. */
    private const val API_URL =
        "https://api.github.com/repos/CosminSebastian2025/FoodApp2/releases/latest"
    private const val USER_AGENT = "FastBite-Android"

    /** Client dedicato con timeout brevi: se GitHub non risponde l'errore arriva subito. */
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    /** Mappatura dei soli campi JSON che ci interessano (tag, note, asset). */
    private data class ReleaseDto(val tag_name: String?, val body: String?, val assets: List<AssetDto>?)
    private data class AssetDto(val name: String?, val browser_download_url: String?, val size: Long?)

    /**
     * Chiama l'API GitHub e restituisce la release con un APK allegato.
     * Ritorna null quando la release non ha un APK valido: in quel caso
     * l'app considera la versione installata come aggiornata.
     */
    fun fetchLatest(): AvailableUpdate? {
        val request = Request.Builder()
            .url(API_URL)
            .header("Accept", "application/vnd.github+json")
            .header("User-Agent", USER_AGENT)
            .build()
        client.newCall(request).execute().use { response ->
            // Un 404 (repo non pubblico) o un 403 (limite API) diventano un errore leggibile.
            if (!response.isSuccessful) error("GitHub ha risposto ${response.code}")
            val release = gson.fromJson(response.body?.string().orEmpty(), ReleaseDto::class.java)
                ?: return null
            // Il tag è "v1.1.8": la UI e il confronto versioni lavorano senza la "v".
            val version = release.tag_name.orEmpty().trim().removePrefix("v").removePrefix("V")
            if (version.isBlank()) return null
            // Tra gli asset (APK e AAB) interessa solo l'APK.
            val apk = release.assets.orEmpty().firstOrNull { asset ->
                asset.name?.endsWith(".apk", ignoreCase = true) == true &&
                    !asset.browser_download_url.isNullOrBlank()
            } ?: return null
            return AvailableUpdate(
                versionName = version,
                notes = release.body.orEmpty().trim(),
                apkUrl = apk.browser_download_url.orEmpty(),
                apkSize = apk.size ?: 0L
            )
        }
    }

    /**
     * Confronto numerico a segmenti: 1.1.10 è più recente di 1.1.9
     * (un confronto testuale direbbe il contrario).
     */
    fun isNewer(remote: String, installed: String): Boolean {
        val remoteParts = versionParts(remote)
        val installedParts = versionParts(installed)
        for (index in 0 until maxOf(remoteParts.size, installedParts.size)) {
            val remoteValue = remoteParts.getOrElse(index) { 0 }
            val installedValue = installedParts.getOrElse(index) { 0 }
            if (remoteValue != installedValue) return remoteValue > installedValue
        }
        return false
    }

    /** "v1.1.8-beta" -> [1, 1, 8]: prende solo la parte numerica di ogni segmento. */
    private fun versionParts(value: String): List<Int> =
        value.trim().removePrefix("v").removePrefix("V").split('.')
            .map { part -> part.takeWhile { it.isDigit() }.toIntOrNull() ?: 0 }

    /**
     * Scarica l'APK in [target] e riporta l'avanzamento 0..100 tramite [onProgress].
     * OkHttp segue automaticamente il redirect verso objects.githubusercontent.com.
     */
    fun download(update: AvailableUpdate, target: File, onProgress: (Int) -> Unit): File {
        val request = Request.Builder()
            .url(update.apkUrl)
            .header("User-Agent", USER_AGENT)
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) error("Download non riuscito (${response.code})")
            val body = response.body ?: error("Risposta vuota")
            // Se GitHub non dichiara la dimensione si usa quella letta dall'API della release.
            val total = body.contentLength().takeIf { it > 0 } ?: update.apkSize
            target.parentFile?.mkdirs()
            body.byteStream().use { input ->
                target.outputStream().use { output ->
                    // Copia a blocchi da 64 KB aggiornando la percentuale man mano.
                    val buffer = ByteArray(64 * 1024)
                    var written = 0L
                    while (true) {
                        val read = input.read(buffer)
                        if (read < 0) break
                        output.write(buffer, 0, read)
                        written += read
                        if (total > 0) {
                            onProgress(((written * 100) / total).coerceIn(0, 100).toInt())
                        }
                    }
                }
            }
        }
        return target
    }
}
