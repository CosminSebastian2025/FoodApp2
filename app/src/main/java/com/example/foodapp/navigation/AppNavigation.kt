package com.example.foodapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.savedstate.SavedState
import com.example.foodapp.data.local.AssetsJsonReader
import com.example.foodapp.data.repository.FastBiteRepository
import com.example.foodapp.ui.theme.components.bottomNavItem

@Composable
fun AddNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    //CartViewModel condiviso
    val context = LocalContext.current
    val reader = remember { AssetsJsonReader(context) }
    val repo = remember { FastBiteRepository(reader) }
    val cartVm: CartViewModel = viewModel(factory = CartViewModelFactory(repo))

    val screensWithBottomBar = bottomNavItem.map { it.route } + listOf("product/{productId}")
    val showBottomBar = currentRoute in screensWithBottomBar

    Scaffold(
        bottombar = {
            if(showBottomBar) {
                FastBiteBottomBar(currentRoute, cartVm) { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) { svaeState= true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        }
    ) {
        padding ->
        NavHost(
            navController, "Home",
            modifier = Modifier.padding(padding)
        ) {
            composable("Home") {
                val vm: HomeViewModel = viewModel(factory = HomeViewModelFactory(repo))
                HomeViewModel(vm,
                    onProductClick = {navController.navigate("product/$it")},
                    onCategory = {navController.navigate("menu?category=$it")},
                    onSearchClick = {navController.navigate("search")}
                )
            }
            composable("menu") {
                val vm: MenuViewModel = viewModel(factory = MenuViewModelFactory(repo))
                MenuViewModel(vm, onProductClick = { navController.navigate("product/$it") })
            }
            composable("search") {
                val vm: SearchViewModel = viewModel(factory = SearchViewModelFactory(repo))
                SearchViewModel(vm, onProductClick = { navController.navigate("product/$it") })
            }
            composable("cart") {
                CartScreen(cartVm,
                    onCheckout = { navController.navigate("checkout") },
                    onBrowserMenu = { navController.navigate("menu") }
                )
            }
            composable("profile") { ProfileScreen() }
            composable("product/{productId}", arguments = listOf(navArgument("productId") { type = object : NavType.StringType })) {
                backStackEntry ->
                val productID = backStackEntry.arguments?.getString("productId") ?: return@composable
                val vm: ProductDetailViewModel = viewModel( factory = ProductViewModelFactory) { }
            }
        }
    }
}