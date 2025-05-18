package com.example.emtyapp.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.emtyapp.data.repository.ProductRepositoryImpl
import com.example.emtyapp.mvi.intent.ViewIntent
import com.example.emtyapp.mvi.viewmodel.HomeViewModel
import com.example.emtyapp.mvi.viewmodel.ProductDetailViewModel
import com.example.emtyapp.mvi.viewmodel.ProductListViewModel
import com.example.emtyapp.ui.HomeScreen
import com.example.emtyapp.ui.ProductDetailScreen
import com.example.emtyapp.ui.ProductListScreen

// Routes
object Routes {
    const val HOME = "home"
    const val PRODUCT_LIST = "product_list"
    const val PRODUCT_DETAIL = "product_detail/{productId}"

    fun createDetailRoute(productId: String) = "product_detail/$productId"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Instanciation des repositories (dans une app réelle, cela serait injecté par Dagger/Hilt)
    val productRepository = remember { ProductRepositoryImpl() }

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            val viewModel: HomeViewModel = viewModel()
            val state by viewModel.state.collectAsState()

            HomeScreen(
                state = state,
                onIntent = { intent ->
                    viewModel.processIntent(intent)
                    if (intent is ViewIntent.HomeIntent.NavigateToProducts) {
                        navController.navigate(Routes.PRODUCT_LIST)
                    }
                }
            )
        }

        composable(Routes.PRODUCT_LIST) {
            val viewModel: ProductListViewModel = viewModel(
                factory = ProductListViewModel.Factory(productRepository)
            )
            val state by viewModel.state.collectAsState()

            // Chargement initial des produits
            LaunchedEffect(key1 = true) {
                viewModel.processIntent(ViewIntent.ProductListIntent.LoadProducts)
            }

            ProductListScreen(
                state = state,
                onIntent = { intent ->
                    viewModel.processIntent(intent)
                    if (intent is ViewIntent.ProductListIntent.ProductSelected) {
                        navController.navigate(Routes.createDetailRoute(intent.productId))
                    } else if (intent is ViewIntent.NavigateBack) {
                        navController.popBackStack()
                    }
                }
            )
        }

        composable(
            Routes.PRODUCT_DETAIL,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            val viewModel: ProductDetailViewModel = viewModel(
                factory = ProductDetailViewModel.Factory(productRepository)
            )
            val state by viewModel.state.collectAsState()

            // Chargement initial des détails du produit
            LaunchedEffect(key1 = productId) {
                viewModel.processIntent(ViewIntent.ProductDetailIntent.LoadProductDetail(productId))
            }

            ProductDetailScreen(
                state = state,
                onIntent = { intent ->
                    viewModel.processIntent(intent)
                    if (intent is ViewIntent.NavigateBack) {
                        navController.popBackStack()
                    }
                }
            )
        }
    }
}