package com.example.foodapp.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.foodapp.retrofit.RetrofitClient

/**
 * Mostra l'immagine del prodotto da URL oppure, se il server restituisce
 * un'emoji/placeholder, la rende come testo.
 */
@Composable
fun ProductImage(
    raw: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    emojiFontSize: TextUnit = 28.sp
) {
    val url = remember(raw) { RetrofitClient.resolveImageUrl(raw.orEmpty()) }
    if (url != null) {
        AsyncImage(
            model = url,
            contentDescription = contentDescription,
            modifier = modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    } else {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(raw?.takeIf { it.isNotBlank() } ?: "🍔", fontSize = emojiFontSize)
        }
    }
}
