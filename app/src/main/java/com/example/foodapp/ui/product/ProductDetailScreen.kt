package com.example.foodapp.ui.product

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.foodapp.ui.cart.CartViewModel
import com.example.foodapp.ui.theme.FastBiteRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    vm: ProductDetailViewModel,
    cartVm: CartViewModel,
    onBack: () -> Unit,
    onAddedToCart: () -> Unit
) {
    val state by vm.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val showAddedDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Indietro")
                    }
                },
                actions = {
                    IconButton(onClick = { /** preferito **/ }) {
                        Icon(Icons.Default.FavoriteBorder, "Preferiti")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        bottomBar = {
            if (state.product != null) {
                BottomAddBar(
                    total = vm.computeTotal(),
                    quantity = state.quantity,
                    enabled = vm.isSelectionValid(),
                    onAdd = {
                        vm.buildCartItem()?.let { cartVm.addItem(it) }
                        showAddedDialog = true
                    }
                )
            }
        }
    ) { padding ->
        when {
            state.isLoading -> {
                Box( Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = FastBiteRed)
                }
            }
            state.error != null -> {
                Box(Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Errore: ${state.error}")
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = onBack) { Text("Torna Indietro") }
                    }
                }
            }
            else -> {
                Column(
                    Modifier
                        .fillMaxSize().
                        .padding(padding)
                        .verticalScroll(rememberScrollState)
                ) {
                    //IMMAGINE PRODOTTO
                    AsyncImage(
                        model = state.product?.imageUrl,
                        contentDescription = state.product?.nameProduct,
                        modifier = Modifier
                            .fillMaxSize()
                            .height(260.dp),
                        contentScale = ContentScale.Crop
                    )

                    Column(Modifier.padding(16.dp)) {}
                }
            }
        }
    }
}

@Composable
fun BottomAddBar(total: Double, quantity: Int, enabled: Boolean, onAdd: () -> Unit) {
    TODO("Not yet implemented")
}