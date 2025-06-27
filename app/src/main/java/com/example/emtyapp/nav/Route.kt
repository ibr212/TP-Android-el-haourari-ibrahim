// nav/AppNavigation.kt - Version mise à jour avec authentification
package com.example.emtyapp.nav

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.emtyapp.data.model.Order
import com.example.emtyapp.mvi.intent.ViewIntent
import com.example.emtyapp.mvi.model.AuthState
import com.example.emtyapp.mvi.intent.ViewIntent.AuthIntent
import com.example.emtyapp.mvi.viewmodel.*
import com.example.emtyapp.ui.*

object Routes {
    // Routes existantes
    const val HOME = "home"
    const val PRODUCT_LIST = "product_list"
    const val PRODUCT_DETAIL = "product_detail/{productId}"
    const val CART = "cart"
    const val ORDER = "order"
    const val ORDER_LIST = "order_list"

    // Nouvelles routes d'authentification
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val PROFILE = "profile"

    fun createDetailRoute(productId: String) = "product_detail/$productId"
}

// Définition des éléments de navigation mis à jour
sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val title: String
) {
    object Home : BottomNavItem(Routes.HOME, Icons.Default.Home, "Accueil")
    object ProductList : BottomNavItem(Routes.PRODUCT_LIST, Icons.Default.ShoppingBag, "Produits")
    object Cart : BottomNavItem(Routes.CART, Icons.Default.ShoppingCart, "Panier")
    object OrderList : BottomNavItem(Routes.ORDER_LIST, Icons.Default.List, "Commandes")
    object Profile : BottomNavItem(Routes.PROFILE, Icons.Default.Person, "Profil")
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.ProductList,
    BottomNavItem.Cart,
    BottomNavItem.OrderList,
    BottomNavItem.Profile
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val cartViewModel: CartViewModel = hiltViewModel()
    val authViewModel: AuthViewModel = hiltViewModel()

    // Observer l'état d'authentification
    val authState by authViewModel.state.collectAsState()

    // État global pour les commandes
    var globalOrders by remember { mutableStateOf<List<Order>>(emptyList()) }

    // Définir la destination de départ selon l'état d'authentification
    val startDestination = if (authState.isLoggedIn) Routes.HOME else Routes.LOGIN

    LaunchedEffect(authState.isLoggedIn) {
        if (!authState.isLoggedIn) {
            // Rediriger vers login si pas connecté
            navController.navigate(Routes.LOGIN) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Scaffold(
        bottomBar = {
            // Afficher la barre de navigation seulement si connecté
            if (authState.isLoggedIn) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            route = "root_graph",
            modifier = Modifier.padding(paddingValues)
        ) {

            /** ---------- AUTHENTIFICATION ---------- **/
            composable(Routes.LOGIN) {
                // Observer les changements d'état d'authentification
                LaunchedEffect(authState.isLoggedIn) {
                    if (authState.isLoggedIn) {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                }

                LoginScreen(
                    state = authState,
                    onIntent = { intent ->
                        authViewModel.processIntent(intent)
                    },
                    onNavigateToRegister = {
                        navController.navigate(Routes.REGISTER)
                    }
                )
            }

            composable(Routes.REGISTER) {
                // Observer les changements d'état d'authentification
                LaunchedEffect(authState.isLoggedIn) {
                    if (authState.isLoggedIn) {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.REGISTER) { inclusive = true }
                        }
                    }
                }

                RegisterScreen(
                    state = authState,
                    onIntent = { intent ->
                        authViewModel.processIntent(intent)
                    },
                    onNavigateToLogin = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Routes.PROFILE) {
                ProfileScreen(
                    user = authState.user,
                    onLogout = {
                        authViewModel.processIntent(AuthIntent.Logout)
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            /** ---------- ÉCRANS EXISTANTS (protégés par authentification) ---------- **/
            composable(Routes.HOME) {
                // Vérifier l'authentification
                if (!authState.isLoggedIn) {
                    LaunchedEffect(Unit) {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                    return@composable
                }

                val viewModel: HomeViewModel = hiltViewModel()
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

            composable(Routes.PRODUCT_LIST) { backStackEntry ->
                if (!authState.isLoggedIn) {
                    LaunchedEffect(Unit) {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                    return@composable
                }

                val productListViewModel: ProductListViewModel = hiltViewModel(backStackEntry)
                val state by productListViewModel.state.collectAsState()

                LaunchedEffect(Unit) {
                    productListViewModel.processIntent(ViewIntent.ProductListIntent.LoadProducts)
                }

                ProductListScreen(
                    state = state,
                    onIntent = { intent ->
                        productListViewModel.processIntent(intent)
                        when (intent) {
                            is ViewIntent.ProductListIntent.ProductSelected ->
                                navController.navigate(Routes.createDetailRoute(intent.productId))

                            is ViewIntent.NavigateBack ->
                                navController.popBackStack()

                            is ViewIntent.NavigateToCart ->
                                navController.navigate(Routes.CART)

                            is ViewIntent.CartIntent -> {
                                Log.d(
                                    "PRODUCT_LIST",
                                    "Forwarding CartIntent to CartViewModel: $intent"
                                )
                                cartViewModel.processIntent(intent)
                            }

                            else -> Unit
                        }
                    }
                )
            }

            composable(Routes.CART) {
                if (!authState.isLoggedIn) {
                    LaunchedEffect(Unit) {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                    return@composable
                }

                val state by cartViewModel.state.collectAsState()

                LaunchedEffect(Unit) {
                    Log.d("CART_SCREEN", "Cart state items: ${state.items.size}")
                }

                CartScreen(
                    state = state,
                    onIntent = { intent ->
                        Log.d("CART_SCREEN", "Intent reçu: $intent")
                        cartViewModel.processIntent(intent)
                    },
                    navController = navController,
                    onOrderCreated = { newOrder ->
                        globalOrders = globalOrders + newOrder
                        Log.d(
                            "NAVIGATION",
                            "Nouvelle commande ajoutée. Total: ${globalOrders.size}"
                        )
                    }
                )
            }

            composable(Routes.ORDER_LIST) {
                if (!authState.isLoggedIn) {
                    LaunchedEffect(Unit) {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                    return@composable
                }

                Log.d("NAVIGATION", "OrderList avec ${globalOrders.size} commandes")

                OrderListScreen(
                    orders = globalOrders,
                    onOrderClick = { order ->
                        navController.currentBackStackEntry?.savedStateHandle?.set("order", order)
                        navController.navigate(Routes.ORDER)
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                Routes.PRODUCT_DETAIL,
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                if (!authState.isLoggedIn) {
                    LaunchedEffect(Unit) {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                    return@composable
                }

                val productId =
                    backStackEntry.arguments?.getString("productId") ?: return@composable
                val viewModel: ProductDetailViewModel = hiltViewModel()
                val state by viewModel.state.collectAsState()

                LaunchedEffect(productId) {
                    viewModel.processIntent(
                        ViewIntent.ProductDetailIntent.LoadProductDetail(productId)
                    )
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

            composable(Routes.ORDER) {
                if (!authState.isLoggedIn) {
                    LaunchedEffect(Unit) {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                    return@composable
                }

                // Retrieve the saved order
                val order = navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<Order>("order")
                    ?: run {
                        // Handle case where order is not found
                        navController.popBackStack()
                        return@composable
                    }

                OrderScreen(
                    order = order,
                    navController = navController,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Ne pas afficher la navbar sur les écrans de détail
    val shouldShowBottomBar = currentDestination?.route?.let { route ->
        !route.startsWith("product_detail") && route != Routes.HOME
    } ?: true

    if (shouldShowBottomBar) {
        NavigationBar {
            bottomNavItems.forEach { item ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title
                        )
                    },
                    label = {
                        Text(
                            text = item.title,
                            maxLines = 1,  // Ensure text stays on one line
                            overflow = TextOverflow.Ellipsis,  // Add ellipsis if text is too long
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp  // Adjust font size if needed
                            )
                        )
                    },
                    selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                    onClick = {
                        if (currentDestination?.route != item.route) {
                            navController.navigate(item.route) {
                                // Éviter la création de plusieurs instances du même écran
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    }
}