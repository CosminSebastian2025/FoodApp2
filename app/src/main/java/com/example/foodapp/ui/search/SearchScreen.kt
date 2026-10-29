package com.fastbite.app.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import coil.compose.AsyncImage
import com.fastbite.app.data.model.Product
import com.fastbite.app.data.repository.FastBiteRepository
import com.fastbite.app.ui.theme.FastBiteRed
import com.fastbite.app.ui.theme.FastBiteTextGray
import com.fastbite.app.ui.theme.FastBiteTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val frequentSearches: List<String> = listOf("Big Mac", "Patatine", "McFlurry", "Chicken McNuggets"),
    val results: List<Product> = emptyList()
)

class SearchViewModel(private val repo: FastBiteRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    fun onQueryChange(q: String) {
        _uiState.value = _uiState.value.copy(query = q)
        if (q.isBlank()) {
            _uiState.value = _uiState.value.copy(results = emptyList())
            return
        }
        viewModelScope.launch {
            val all = repo.getAllProducts()
            val filtered = all.filter { it.name.contains(q, ignoreCase = true) }
            _uiState.value = _uiState.value.copy(results = filtered)
        }
    }
}

class SearchViewModelFactory(private val repo: FastBiteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = SearchViewModel(repo) as T
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(vm: SearchViewModel, onProductClick: (String) -> Unit = {}) {
    val state by vm.uiState.collectAsState()

    Scaffold(topBar = {
        TopAppBar(title = { Text("Cerca nel Menu", fontWeight = FontWeight.Bold) })
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            // Campo ricerca
            OutlinedTextField(
                value = state.query,
                onValueChange = vm::onQueryChange,
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                placeholder = { Text("Cerca un piatto...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = {
                    if (state.query.isNotEmpty()) IconButton(onClick = { vm.onQueryChange("") }) {
                        Icon(Icons.Default.Clear, "Cancella")
                    }
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            if (state.query.isBlank()) {
                // Ricerche frequenti
                Text("Ricerche Frequenti", fontWeight = FontWeight.Bold, fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(Modifier.height(8.dp))
                state.frequentSearches.forEach { term ->
                    ListItem(
                        headlineContent = { Text(term) },
                        leadingContent = { Icon(Icons.Default.History, null, tint = FastBiteTextGray) },
                        modifier = Modifier.clickable { vm.onQueryChange(term) }
                    )
                }
            } else if (state.results.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.SearchOff, null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                        Spacer(Modifier.height(8.dp))
                        Text("Nessun piatto trovato per \"${state.query}\"", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn {
                    items(state.results) { p ->
                        ListItem(
                            headlineContent = { Text(p.name, fontWeight = FontWeight.SemiBold) },
                            supportingContent = { Text("€ ${"%.2f".format(p.basePrice)}", color = FastBiteRed) },
                            leadingContent = {
                                AsyncImage(model = p.imageUrl, contentDescription = null,
                                    modifier = Modifier.size(56.dp))
                            },
                            modifier = Modifier.clickable { onProductClick(p.id) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Search - Vuoto")
@Composable
private fun SearchEmptyPreview() {
    FastBiteTheme {
        Column {
            OutlinedTextField(value = "", onValueChange = {},
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                placeholder = { Text("Cerca un piatto...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                shape = RoundedCornerShape(12.dp))
            Text("Ricerche Frequenti", fontWeight = FontWeight.Bold, fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 16.dp))
            listOf("Big Mac", "Patatine", "McFlurry").forEach {
                ListItem(
                    headlineContent = { Text(it) },
                    leadingContent = { Icon(Icons.Default.History, null) }
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Search - Con risultati")
@Composable
private fun SearchResultsPreview() {
    FastBiteTheme {
        Column {
            OutlinedTextField(value = "Big", onValueChange = {},
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                leadingIcon = { Icon(Icons.Default.Search, null) },
                shape = RoundedCornerShape(12.dp))
            ListItem(
                headlineContent = { Text("Big Mac", fontWeight = FontWeight.SemiBold) },
                supportingContent = { Text("€ 5.99", color = FastBiteRed) },
                leadingContent = { Surface(Modifier.size(56.dp), color = Color(0xFFEEEEEE)) {} }
            )
        }
    }
}