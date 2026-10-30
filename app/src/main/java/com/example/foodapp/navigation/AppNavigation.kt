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
import com.example.foodapp.data.local.AssetsJsonReader
import com.example.foodapp.data.local.LocalApiImpl
import com.example.foodapp.data.repository.FastBiteRepository
import com.example.foodapp.ui.cart.AddedToCartScreen
import com.example.foodapp.ui.cart.CartScreen
import com.example.foodapp.ui.cart.CartViewModel
import com.example.foodapp.ui.cart.CartViewModelFactory
import com.example.foodapp.ui.checkout.CheckoutScreen
import com.example.foodapp.ui.components.FastBiteBottomBar
import com.example.foodapp.ui.components.bottomNavItems
import com.example.foodapp.ui.confirmation.ConfirmationScreen
import com.example.foodapp.ui.home.HomeScreen
import com.example.foodapp.ui.home.HomeViewModel
import com.example.foodapp.ui.home.HomeViewModelFactory
import com.example.foodapp.ui.menu.MenuScreen
import com.example.foodapp.ui.menu.MenuViewModel
import com.example.foodapp.ui.menu.MenuViewModelFactory
import com.example.foodapp.ui.product.ProductDetailScreen
import com.example.foodapp.ui.product.ProductDetailViewModel
import com.example.foodapp.ui.product.ProductDetailViewModelFactory
import com.example.foodapp.ui.profile.ProfileScreen
import com.example.foodapp.ui.search.SearchScreen
import com.example.foodapp.ui.search.SearchViewModel
import com.example.foodapp.ui.search.SearchViewModelFactory


@Composable
fun AddNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    //CartViewModel condiviso
    val context = LocalContext.current
    val reader = remember { AssetsJsonReader(context) }
    val api = remember { LocalApiImpl(reader) }
    val repo = remember { FastBiteRepository(api) }

    val cartVm: CartViewModel = viewModel(factory = CartViewModelFactory(repo))

    val screensWithBottomBar = bottomNavItems.map { it.route } + listOf("product/{productId}")
    val showBottomBar = currentRoute in screensWithBottomBar

    Scaffold(
        bottomBar = {
            if(showBottomBar) {
                FastBiteBottomBar(currentRoute, cartVm) { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
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
                HomeScreen(
                    vm,
                    onProductClick = {navController.navigate("product/$it")},
                    onCategoryClick = { navController.navigate("menu?category=$it") },
                    onSearchClick = {navController.navigate("search")}
                )
            }
            composable("menu") {
                val vm: MenuViewModel = viewModel(factory = MenuViewModelFactory(repo))
                MenuScreen(vm, onProductClick = { navController.navigate("product/$it") })
            }
            composable("search") {
                val vm: SearchViewModel = viewModel(factory = SearchViewModelFactory(repo))
                SearchScreen(vm, onProductClick = { navController.navigate("product/$it") })
            }
            composable("cart") {
                CartScreen(cartVm,
                    onCheckout = { navController.navigate("checkout") },
                    onBrowseMenu = { navController.navigate("menu") }
                )
            }
            composable("profile") { ProfileScreen() }

            composable(
                "product/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) {
                backStackEntry ->
                val productID = backStackEntry.arguments?.getString("productId") ?: return@composable
                val vm: ProductDetailViewModel =
                    viewModel(factory = ProductDetailViewModelFactory(productID, repo))
                ProductDetailScreen(
                    vm = vm,
                    cartVm = cartVm,
                    onBack = { navController.popBackStack() },
                    onAddedToCart = { item ->
                        navController.navigate("addedToCart/${item.productId}/${item.quantity}")
                    }
                )
            }

            composable(
                "addedToCart/{productId}/{quantity}",
                arguments = listOf(
                    navArgument("productId") { type = NavType.StringType },
                    navArgument("quantity") { type = NavType.IntType }
                )
            ) { backEntryStack ->
                val productId =
                    backEntryStack.arguments?.getString("productId") ?: return@composable
                val quantity = backEntryStack.arguments?.getInt("quantity") ?: 1

                val lastItem = cartVm.items.value.lastOrNull { it.productId == productId }

                if (lastItem != null) {
                    AddedToCartScreen(
                        item = lastItem,
                        onGoToCart = {
                            navController.navigate("cart") {
                                popUpTo("Home") { inclusive = false }
                            }
                        },
                        onContinueShopping = { navController.popBackStack() }
                    )
                }
            }

            composable("checkout") {
                CheckoutScreen(
                    total = cartVm.calculateTotals().total,
                    onSubmit = { navController.navigate("confirmation") { popUpTo("Home") } }
                )
            }

            composable("confirmation") {
                ConfirmationScreen(onBackHome = {
                    cartVm.clearCart()
                    navController.navigate("Home") { popUpTo(0) { inclusive = true } }
                })
            }
        }
    }
}