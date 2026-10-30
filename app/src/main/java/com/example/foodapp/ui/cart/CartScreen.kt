package com.example.foodapp.ui.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.foodapp.data.model.CartItem
import com.example.foodapp.data.model.CartRules
import com.example.foodapp.data.repository.FastBiteRepository
import com.example.foodapp.ui.Theme.FastBiteRed
import com.example.foodapp.ui.Theme.FastBiteTextGray
import com.example.foodapp.ui.Theme.FastBiteTheme
import com.example.foodapp.ui.components.EmptyStateView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CartTotals(val subtotal: Double, val discount: Double,
                      val managementFee: Double, val pickupFee: Double, val total: Double)

class CartViewModel(private val repo: FastBiteRepository) : ViewModel() {
    private val _items = MutableStateFlow<List<CartItem>>(emptyList())
    val items: StateFlow<List<CartItem>> = _items.asStateFlow()

    private val _coupon = MutableStateFlow<String?>(null)
    val coupon: StateFlow<String?> = _coupon.asStateFlow()

    private val _discountPercent = MutableStateFlow(0)
    val discountPercent: StateFlow<Int> = _discountPercent.asStateFlow()

    private val _rules = MutableStateFlow<CartRules?>(null)
    val rules: StateFlow<CartRules?> = _rules.asStateFlow()

    private val _itemCount = MutableStateFlow(0)
    val itemCount: StateFlow<Int> = _itemCount.asStateFlow()

    init {
        viewModelScope.launch {
            _rules.value = repo.getCartRules()
            _items.collect { _itemCount.value = it.sumOf { i -> i.quantity } }
        }
    }

    fun addItem(item: CartItem) { _items.value = _items.value + item }
    fun updateQuantity(i: Int, d: Int) {
        val list = _items.value.toMutableList()
        if (i !in list.indices) return
        val nq = list[i].quantity + d
        if (nq <= 0) list.removeAt(i) else list[i] = list[i].copy(quantity = nq)
        _items.value = list
    }
    fun removeItem(i: Int) {
        val list = _items.value.toMutableList()
        if (i in list.indices) { list.removeAt(i); _items.value = list }
    }
    fun clearCart() { _items.value = emptyList(); _coupon.value = null; _discountPercent.value = 0 }

    suspend fun applyCoupon(code: String): Result<Int> {
        val rules = _rules.value ?: return Result.failure(Exception("Regole non caricate"))
        return if (code.uppercase() == rules.discountCode) {
            _coupon.value = code.uppercase(); _discountPercent.value = rules.discountPercent
            Result.success(rules.discountPercent)
        } else Result.failure(Exception("Codice non valido"))
    }

    fun removeCoupon() { _coupon.value = null; _discountPercent.value = 0 }

    fun calculateTotals(): CartTotals {
        val r = _rules.value ?: return CartTotals(0.0, 0.0, 0.50, 0.0, 0.50)
        val subtotal = _items.value.sumOf { it.totalPrice }
        val discount = subtotal * _discountPercent.value / 100.0
        val total = subtotal - discount + r.managementFee + r.pickupFee
        return CartTotals(subtotal, discount, r.managementFee, r.pickupFee, total)
    }
}

class CartViewModelFactory(private val repo: FastBiteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = CartViewModel(repo) as T
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(vm: CartViewModel, onCheckout: () -> Unit = {}, onBrowseMenu: () -> Unit = {}) {
    val items by vm.items.collectAsState()
    val coupon by vm.coupon.collectAsState()
    val discount by vm.discountPercent.collectAsState()
    val totals = vm.calculateTotals()
    var couponInput by remember { mutableStateOf("") }
    var couponError by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(topBar = {
        TopAppBar(title = { Text("Riepilogo Asporto", fontWeight = FontWeight.Bold) },
            actions = {
                if (items.isNotEmpty()) IconButton(onClick = { vm.clearCart() }) {
                    Icon(Icons.Default.Delete, "Svuota")
                }
            })
    }) { padding ->
        if (items.isEmpty()) {
            Box(Modifier
                .fillMaxSize()
                .padding(padding), contentAlignment = Alignment.Center) {
                EmptyStateView(Icons.Default.ShoppingCart, "Il tuo carrello è vuoto",
                    "Aggiungi prodotti dal menu per iniziare", "Sfoglia Menu", onBrowseMenu)
            }
        } else {
            Column(Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())) {
                // Lista articoli
                items.forEachIndexed { i, item ->
                    CartItemRow(item,
                        onPlus = { vm.updateQuantity(i, 1) },
                        onMinus = { vm.updateQuantity(i, -1) },
                        onRemove = { vm.removeItem(i) })
                }

                // Codice sconto
                Card(Modifier
                    .fillMaxWidth()
                    .padding(16.dp), shape = RoundedCornerShape(12.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalOffer, null, tint = FastBiteRed)
                            Spacer(Modifier.width(8.dp))
                            Text("Codice Sconto", fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = couponInput,
                                onValueChange = { couponInput = it; couponError = null },
                                modifier = Modifier.weight(1f),
                                placeholder = { Text("Es. FAST10") },
                                singleLine = true, shape = RoundedCornerShape(8.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Button(onClick = {
                                coroutineScope.launch {
                                    vm.applyCoupon(couponInput)
                                        .onSuccess { couponError = null }
                                        .onFailure { couponError = it.message }
                                }
                            }, shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FastBiteRed)) {
                                Text("Applica", color = Color.White)
                            }
                        }
                        couponError?.let { Text(it, color = FastBiteRed, fontSize = 12.sp) }
                        coupon?.let {
                            Spacer(Modifier.height(8.dp))
                            Surface(color = Color(0xFFE8F5E9), shape = RoundedCornerShape(6.dp)) {
                                Text("✅ Sconto $discount% applicato all'asporto!",
                                    color = Color(0xFF2E7D32), fontSize = 13.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                            }
                        }
                    }
                }

                // Totali
                Card(Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        SummaryRow("Subtotale articoli", "€${"%.2f".format(totals.subtotal)}")
                        if (totals.discount > 0) SummaryRow("Sconto FAST10 (-$discount%)",
                            "-€${"%.2f".format(totals.discount)}", Color(0xFF2E7D32))
                        SummaryRow("Costo gestione asporto", "€${"%.2f".format(totals.managementFee)}")
                        SummaryRow("Ritiro in cassa", "Gratuito", Color(0xFF2E7D32))
                        HorizontalDivider(Modifier.padding(vertical = 8.dp))
                        SummaryRow("Totale Finale", "€${"%.2f".format(totals.total)}",
                            bold = true, color = FastBiteRed)
                    }
                }

                Button(
                    onClick = onCheckout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FastBiteRed)
                ) {
                    Text("Procedi al Ritiro", color = Color.White, fontSize = 16.sp,
                        fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun CartItemRow(item: CartItem, onPlus: () -> Unit, onMinus: () -> Unit, onRemove: () -> Unit) {
    Card(Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp)) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(item.productName, fontWeight = FontWeight.SemiBold)
                if (item.selectedOptions.isNotEmpty()) {
                    Text(item.selectedOptions.joinToString(", "),
                        fontSize = 12.sp, color = FastBiteTextGray)
                }
                Spacer(Modifier.height(4.dp))
                Text("€${"%.2f".format(item.unitPrice)} × ${item.quantity}",
                    fontSize = 13.sp, color = FastBiteTextGray)
            }
            Column(horizontalAlignment = Alignment.End) {
                IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, null, modifier = Modifier.size(16.dp))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onMinus, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Remove, null, modifier = Modifier.size(16.dp))
                    }
                    Text("${item.quantity}", fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp))
                    IconButton(onClick = onPlus, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                    }
                }
                Text("€${"%.2f".format(item.totalPrice)}", fontWeight = FontWeight.Bold,
                    color = FastBiteRed)
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String, color: Color = Color.Unspecified, bold: Boolean = false) {
    Row(Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = FastBiteTextGray,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal)
        Text(value, color = color, fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal)
    }
}

// ===== PREVIEW =====
@Preview(showBackground = true, showSystemUi = true, name = "Cart - Vuoto")
@Composable
private fun CartEmptyPreview() {
    FastBiteTheme {
        EmptyStateView(Icons.Default.ShoppingCart, "Il tuo carrello è vuoto",
            "Aggiungi prodotti dal menu", "Sfoglia Menu", {})
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Cart - Con articoli")
@Composable
private fun CartItemsPreview() {
    FastBiteTheme {
        Column(Modifier.padding(16.dp)) {
            CartItemRow(CartItem("p1", "Big Mac", 5.99, 2, listOf("maxi", "bacon"), 1.50),
                onPlus = {}, onMinus = {}, onRemove = {})
            Spacer(Modifier.height(8.dp))
            CartItemRow(CartItem("p2", "Patatine", 2.50, 1, emptyList(), 0.0),
                onPlus = {}, onMinus = {}, onRemove = {})
            Spacer(Modifier.height(16.dp))
            SummaryRow("Subtotale", "€14.98")
            SummaryRow("Sconto FAST10", "-€1.50", Color(0xFF2E7D32))
            SummaryRow("Gestione asporto", "€0.50")
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            SummaryRow("Totale", "€13.98", FastBiteRed, bold = true)
        }
    }
}