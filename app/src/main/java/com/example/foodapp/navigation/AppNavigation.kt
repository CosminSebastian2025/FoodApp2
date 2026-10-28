package com.example.foodapp.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.foodapp.data.local.AssetsJsonReader
import com.example.foodapp.data.local.LocalApiImpl
import com.example.foodapp.data.repository.FastBiteRepository
import com.example.foodapp.ui.home.HomeScreen
import com.example.foodapp.ui.home.HomeViewModel
import com.example.foodapp.ui.home.HomeViewModelFactory


@Composable
fun AppNavigation(context: android.content.Context) {
    val navController = rememberNavController()

    // Dipendenza injection "manuale" (senza Hilt per ora)
    val reader = AssetsJsonReader(context)
    val api = LocalApiImpl(reader)
    val repo = FastBiteRepository(api)

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            val vm: HomeViewModel = viewModel(
                factory = HomeViewModelFactory(repo)
            )
            HomeScreen(
                vm = vm,
                onProductClick = {
                    productId -> navController.navigate("product/$productId")
                },
                onCategoryClick = {
                    categoryId -> navController.navigate("menu?category=$categoryId")
                },
                onSearchClick = {
                    navController.navigate("search")
                }
            )
        }
    }
}