package com.example.foodapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.foodapp.data.model.Category
import com.example.foodapp.data.model.Product
import com.example.foodapp.data.model.Promotion
import com.example.foodapp.ui.theme.FastBiteGray
import com.example.foodapp.ui.theme.FastBiteRed
import com.example.foodapp.ui.theme.FastBiteTextGray
import com.example.foodapp.ui.theme.FastBiteYellow


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(vm: HomeViewModel, onProductClick: (String) -> Unit, onCategoryClick: (String) -> Unit, onSearchClick: () -> Unit) {

    val state by vm.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("FastBite", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = FastBiteRed)
                        Spacer(Modifier.width(8.dp))
                        Surface(shape = CircleShape, color = FastBiteYellow) {
                            Text("🔥", modifier = Modifier.padding(4.dp), fontSize = 14.sp)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onSearchClick){
                        Icon(Icons.Default.Search, "Cerca")
                    }
                    IconButton(onClick = { /** profilo **/ }) {
                        Icon(Icons.Default.Person, "Profilo")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = FastBiteRed)
                }
            }
            state.error != null -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Errore: ${state.error}")
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { vm.loadData() }){ Text("Riprova") }
                    }
                }
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 12.dp),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    //BANNER PROMOZIONALI -- occupa tutta la larghezza
                    item(span = { GridItemSpan(2) }) {
                        PromotionBanner(state.promotions.firstOrNull())
                    }

                    // CATEGORIE -- tutta la larghezza, scroll orizzontale
                    item(span = { GridItemSpan(2) }) {
                        CategoriesSection(state.category, onCategoryClick)
                    }

                    item(span = { GridItemSpan(2) }) {
                        SectionHeader(
                            title = "I Più Ordinati da Asporto",
                            action = "Vedi Tutti",
                            onActionClick = { /** TODO **/ }
                        )
                    }

                    // Prodotti popolari
                    items(state.popularProducts) { product ->
                        ProductCard(product, onClick = { onProductClick(product.IdProduct) })
                    }
                }
            }
        }
    }
}

// ===== COMPONENTI =====
@Composable
fun PromotionBanner(promo: Promotion?) {
    promo ?: return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable{ /** apre dettaglio promo **/ },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box {
            AsyncImage(
                model = promo.ImageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f)))
                    )

            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Surface(
                    color = FastBiteRed,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        "Solo Asporto",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    promo.title,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    promo.subtitle,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(8.dp))
                Row (horizontalArrangement = Arrangement.spacedBy(6.dp)){
                    promo.badges.take(2).forEach{ badge ->
                        Surface(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                badge,
                                color = Color.White,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoriesSection(categories: List<Category>, onClick: (String) -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =  Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Categorie", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            TextButton(onClick = { /** Vedi Tutte **/ }) {Text("Vedi Tutte")}
        }
        LazyRow (
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(categories) { cat ->
                CategoryChip(cat, onClick = { onClick(cat.idCategory) })
            }
        }
    }
}

@Composable
fun CategoryChip(category: Category, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(72.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = FastBiteGray,
        ){
            Box(contentAlignment = Alignment.Center) {
                Text(category.icon, fontSize = 28.sp)
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            category.nameCategory,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            "${category.count} piatti",
            fontSize = 10.sp,
            color = FastBiteTextGray
        )
    }
}

@Composable
fun SectionHeader(title: String, action: String, onActionClick: () -> Unit) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        TextButton(onClick = onActionClick) { Text(action) }
    }
}

@Composable
fun ProductCard(product: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column{
            Box {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.nameProduct,
                    modifier = Modifier
                        .fillMaxSize()
                        .height(110.dp),
                    contentScale = ContentScale.Crop
                )
                if (product.isPopular) {
                    Surface(
                        color = FastBiteYellow,
                        shape = RoundedCornerShape(bottomEnd = 8.dp),
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            "⭐ Popolare",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }
            Column(Modifier.padding(10.dp)) {
                Text(
                    product.nameProduct,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "€ ${"%.2f"}.format(product.basePrice)",
                    color = FastBiteRed,
                    fontSize = 15.sp
                )
            }
        }
    }
}