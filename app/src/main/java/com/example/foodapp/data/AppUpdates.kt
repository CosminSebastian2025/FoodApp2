package com.example.foodapp.data

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.concurrent.TimeUnit

data class AvailableUpdate(
    val versionName: String,
    val notes: String,
    val apkUrl: String,
    val apkSize: Long
)

/** Legge l'ultima release pubblica da GitHub e scarica l'APK. */
object AppUpdates {

    const val RELEASES_PAGE = "https://github.com/CosminSebastian2025/FoodApp2/releases/latest"
    private const val API_URL =
        "https://api.github.com/repos/CosminSebastian2025/FoodApp2/releases/latest"
    private const val USER_AGENT = "FastBite-Android"

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    private data class ReleaseDto(val tag_name: String?, val body: String?, val assets: List<AssetDto>?)
    private data class AssetDto(val name: String?, val browser_download_url: String?, val size: Long?)

    /** Ultima release con un APK allegato, oppure null se non c'è nulla di valido. */
    fun fetchLatest(): AvailableUpdate? {
        val request = Request.Builder()
            .url(API_URL)
            .header("Accept", "application/vnd.github+json")
            .header("User-Agent", USER_AGENT)
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) error("GitHub ha risposto ${response.code}")
            val release = gson.fromJson(response.body?.string().orEmpty(), ReleaseDto::class.java)
                ?: return null
            val version = release.tag_name.orEmpty().trim().removePrefix("v").removePrefix("V")
            if (version.isBlank()) return null
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

    /** Confronto numerico: 1.1.10 è più recente di 1.1.9. */
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

    private fun versionParts(value: String): List<Int> =
        value.trim().removePrefix("v").removePrefix("V").split('.')
            .map { part -> part.takeWhile { it.isDigit() }.toIntOrNull() ?: 0 }

    fun download(update: AvailableUpdate, target: File, onProgress: (Int) -> Unit): File {
        val request = Request.Builder()
            .url(update.apkUrl)
            .header("User-Agent", USER_AGENT)
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) error("Download non riuscito (${response.code})")
            val body = response.body ?: error("Risposta vuota")
            val total = body.contentLength().takeIf { it > 0 } ?: update.apkSize
            target.parentFile?.mkdirs()
            body.byteStream().use { input ->
                target.outputStream().use { output ->
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
