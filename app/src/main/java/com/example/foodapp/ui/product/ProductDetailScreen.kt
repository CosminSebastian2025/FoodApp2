package com.example.foodapp.ui.product

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import coil3.compose.AsyncImage
import com.example.foodapp.data.model.CartItem
import com.example.foodapp.data.model.OptionGroup
import com.example.foodapp.data.model.Product
import com.example.foodapp.data.model.ProductOptionsResponse
import com.example.foodapp.data.repository.FastBiteRepository
import com.example.foodapp.ui.Theme.FastBiteGray
import com.example.foodapp.ui.Theme.FastBiteRed
import com.example.foodapp.ui.Theme.FastBiteTextGray
import com.example.foodapp.ui.Theme.FastBiteTheme
import com.example.foodapp.ui.cart.CartViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProductDetailUiState(
    val isLoading: Boolean = true,
    val product: Product? = null,
    val options: ProductOptionsResponse? = null,
    val selectedOptions: Map<String, Set<String>> = emptyMap(),
    val quantity: Int = 1,
    val error: String? = null
)

class ProductDetailViewModel(
    private val productId: String,
    private val repo: FastBiteRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    init { loadProduct() }

    private fun loadProduct() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val product = repo.getProductById(productId) ?: throw Exception("Prodotto non trovato")
                val options = repo.getProductOptions(productId) ?: ProductOptionsResponse(productId, emptyList())
                val preselected = options.groups
                    .filter { it.selectionType == "single" && it.choises.isNotEmpty() }
                    .associate { it.groupId to setOf(it.choises.first().id) }
                _uiState.value = ProductDetailUiState(
                    isLoading = false, product = product, options = options,
                    selectedOptions = preselected
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun selectSingle(groupId: String, choiceId: String) {
        val current = _uiState.value.selectedOptions.toMutableMap()
        current[groupId] = setOf(choiceId)
        _uiState.value = _uiState.value.copy(selectedOptions = current)
    }

    fun toggleMultiple(groupId: String, choiceId: String) {
        val current = _uiState.value.selectedOptions.toMutableMap()
        val selected = current[groupId]?.toMutableSet() ?: mutableSetOf()
        if (choiceId in selected) selected.remove(choiceId) else selected.add(choiceId)
        current[groupId] = selected
        _uiState.value = _uiState.value.copy(selectedOptions = current)
    }

    fun changeQuantity(delta: Int) {
        val newQty = (_uiState.value.quantity + delta).coerceIn(1, 20)
        _uiState.value = _uiState.value.copy(quantity = newQty)
    }

    fun computeUnitPrice(): Double {
        val product = _uiState.value.product ?: return 0.0
        val options = _uiState.value.options ?: return product.basePrice
        var modifier = 0.0
        for (group in options.groups) {
            val selected = _uiState.value.selectedOptions[group.groupId] ?: emptySet()
            for (choice in group.choises) if (choice.id in selected) modifier += choice.priceModifier
        }
        return product.basePrice + modifier
    }

    fun computeTotal(): Double = computeUnitPrice() * _uiState.value.quantity

    fun isSelectionValid(): Boolean {
        val options = _uiState.value.options ?: return false
        return options.groups.filter { it.isRequired }
            .all { g -> !(_uiState.value.selectedOptions[g.groupId] ?: emptySet()).isEmpty() }
    }

    fun buildCartItem(): CartItem? {
        val p = _uiState.value.product ?: return null
        val s = _uiState.value
        return CartItem(
            p.IdProduct, p.nameProduct, p.basePrice, s.quantity,
            s.selectedOptions.values.flatten(), computeUnitPrice() - p.basePrice)
    }
}

class ProductDetailViewModelFactory(
    private val productId: String, private val repo: FastBiteRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        ProductDetailViewModel(productId, repo) as T
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    vm: ProductDetailViewModel,
    cartVm: CartViewModel,
    onBack: () -> Unit = {},
    onAddedToCart: (CartItem) -> Unit = {}
) {
    val state by vm.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } },
                actions = { IconButton(onClick = {}) { Icon(Icons.Default.FavoriteBorder, null) } }
            )
        },
        bottomBar = {
            if (state.product != null) {
                Surface(shadowElevation = 8.dp) {
                    Column(Modifier.padding(12.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { vm.changeQuantity(-1) }, enabled = state.quantity > 1) {
                                Icon(Icons.Default.Remove, null)
                            }
                            Text("${state.quantity}", style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 20.dp))
                            IconButton(onClick = { vm.changeQuantity(1) }) { Icon(Icons.Default.Add, null) }
                        }
                        Spacer(Modifier.height(4.dp))
                        Button(
                            onClick = {
                                vm.buildCartItem()?.let { item ->
                                    cartVm.addItem(item)
                                    onAddedToCart(item)
                                }
                            },
                            enabled = vm.isSelectionValid(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FastBiteRed)
                        ) {
                            Text("Aggiungi • €${"%.2f".format(vm.computeTotal())}",
                                fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    ) { padding ->
        when {
            state.isLoading -> Box(Modifier
                .fillMaxSize()
                .padding(padding),
                contentAlignment = Alignment.Center) { CircularProgressIndicator(color = FastBiteRed) }
            state.error != null -> Box(Modifier
                .fillMaxSize()
                .padding(padding),
                contentAlignment = Alignment.Center) { Text("Errore: ${state.error}") }
            else -> Column(Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())) {
                AsyncImage(model = state.product?.imageUrl, contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp), contentScale = ContentScale.Crop)
                Column(Modifier.padding(16.dp)) {
                    Text(
                        state.product?.nameProduct ?: "",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text("€ ${"%.2f".format(state.product?.basePrice ?: 0.0)}",
                        color = FastBiteRed, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    if (!state.product?.description.isNullOrBlank()) {
                        Spacer(Modifier.height(12.dp))
                        Text(state.product!!.description, color = FastBiteTextGray)
                    }
                    if (!state.product?.allergens.isNullOrEmpty()) {
                        Spacer(Modifier.height(12.dp))
                        Surface(color = FastBiteGray, shape = RoundedCornerShape(8.dp)) {
                            Row(Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, null, modifier = Modifier.size(16.dp),
                                    tint = FastBiteTextGray)
                                Spacer(Modifier.width(6.dp))
                                Text("Allergeni: ${state.product!!.allergens.joinToString(", ")}",
                                    fontSize = 12.sp, color = FastBiteTextGray)
                            }
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(16.dp))
                    state.options?.groups?.forEach { group ->
                        OptionGroupSection(
                            group = group,
                            selected = state.selectedOptions[group.groupId] ?: emptySet(),
                            onSingle = { vm.selectSingle(group.groupId, it) },
                            onToggle = { vm.toggleMultiple(group.groupId, it) }
                        )
                        Spacer(Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun OptionGroupSection(
    group: OptionGroup, selected: Set<String>,
    onSingle: (String) -> Unit, onToggle: (String) -> Unit
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(group.groupName, style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            if (group.isRequired) Surface(color = FastBiteRed.copy(alpha = 0.1f),
                shape = RoundedCornerShape(6.dp)) {
                Text("Obbligatorio", color = FastBiteRed, fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
            }
        }
        Spacer(Modifier.height(10.dp))
        group.choises.forEach { choice ->
            val isSelected = choice.id in selected
            Surface(
                onClick = { if (group.selectionType == "single") onSingle(choice.id) else onToggle(choice.id) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) FastBiteRed.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, if (isSelected) FastBiteRed else Color.LightGray.copy(alpha = 0.5f))
            ) {
                Row(Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    if (group.selectionType == "multiple") Checkbox(
                        checked = isSelected, onCheckedChange = { onToggle(choice.id) },
                        colors = CheckboxDefaults.colors(checkedColor = FastBiteRed))
                    else RadioButton(selected = isSelected, onClick = { onSingle(choice.id) },
                        colors = RadioButtonDefaults.colors(selectedColor = FastBiteRed))
                    Spacer(Modifier.width(8.dp))
                    Text(choice.name, modifier = Modifier.weight(1f),
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal)
                    if (choice.priceModifier > 0) Text("+€${"%.2f".format(choice.priceModifier)}",
                        color = FastBiteTextGray, fontSize = 13.sp)
                    else if (choice.priceModifier < 0) Text("-€${"%.2f".format(-choice.priceModifier)}",
                        color = Color(0xFF2E7D32), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// ===== PREVIEW BIANCA (vuota) =====
@Preview(showBackground = true, showSystemUi = true, name = "Product Detail - Vuota")
@Composable
private fun ProductDetailEmptyPreview() {
    FastBiteTheme {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = FastBiteRed)
        }
    }
}