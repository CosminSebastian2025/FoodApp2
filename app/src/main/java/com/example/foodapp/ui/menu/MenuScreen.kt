package com.example.foodapp.ui.menu

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodapp.data.model.Product
import com.example.foodapp.ui.components.ErrorBanner
import com.example.foodapp.ui.components.ProductImage

@Composable
fun MenuScreen(menuViewModel: MenuViewModel = viewModel()) {

    // Raccoglie gli stati dal ViewModel
    val products by menuViewModel.prodotti.collectAsState()
    val isLoading by menuViewModel.isLoading.collectAsState()
    val errorMessage by menuViewModel.errorMessage.collectAsState()

    // Carica i prodotti quando la schermata viene mostrata
    LaunchedEffect(Unit) {
        menuViewModel.loadData()
    }

    // UI principale
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Mostra messaggio di errore se c'è
        errorMessage?.let { error ->
            ErrorBanner(
                message = error,
                modifier = Modifier.padding(bottom = 16.dp),
                onRetry = { menuViewModel.loadData() }
            )
        }

        // Mostra loading o lista prodotti
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (products.isEmpty() && errorMessage == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Nessun prodotto disponibile")
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items = products) { product ->
                    ProductCard(product)
                }
            }
        }
    }
}

@Composable
fun ProductCard(product: Product) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Immagine del prodotto
            ProductImage(
                raw = product.imageUrl,
                contentDescription = product.nameProduct,
                modifier = Modifier.size(80.dp),
                emojiFontSize = 36.sp
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Informazioni del prodotto
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.nameProduct,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "€ ${product.basePrice}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
