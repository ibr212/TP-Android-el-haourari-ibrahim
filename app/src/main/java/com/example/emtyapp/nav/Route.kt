package com.example.emtyapp.nav

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.emtyapp.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items

// Modèle de données
data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val description: String,
    val imageP: Int// Optionnel pour les images
)

// Données exemple
val sampleProducts = listOf(
    Product("1", "Smartphone", 599.99, "Écran 6.5\", 128GB Stockage", R.drawable.smartphone),
    Product("2", "Ordinateur", 1299.99, "16GB RAM, SSD 512GB", R.drawable.ordinateur),
    Product("3", "Casque Audio", 249.99, "Réduction de bruit active", R.drawable.casque),
    Product("4", "Smartphone", 599.99, "Écran 6.5\", 128GB Stockage", R.drawable.smartphone),
    Product("5", "Ordinateur", 1299.99, "16GB RAM, SSD 512GB", R.drawable.ordinateur),
    Product("6", "Casque Audio", 249.99, "Réduction de bruit active", R.drawable.casque),
    Product("7", "Smartphone", 599.99, "Écran 6.5\", 128GB Stockage", R.drawable.smartphone),
    Product("8", "Ordinateur", 1299.99, "16GB RAM, SSD 512GB", R.drawable.ordinateur),
    Product("9", "Casque Audio", 249.99, "Réduction de bruit active", R.drawable.casque),
    Product("10", "Smartphone", 599.99, "Écran 6.5\", 128GB Stockage", R.drawable.smartphone),
    Product("11", "Ordinateur", 1299.99, "16GB RAM, SSD 512GB", R.drawable.ordinateur),
    Product("12", "Casque Audio", 249.99, "Réduction de bruit active", R.drawable.casque)
)

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

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToProducts = {
                    navController.navigate(Routes.PRODUCT_LIST)
                }
            )
        }

        composable(Routes.PRODUCT_LIST) {
            ProductListScreen(
                products = sampleProducts,
                onProductClick = { productId ->
                    navController.navigate(Routes.createDetailRoute(productId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            Routes.PRODUCT_DETAIL,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            ProductDetailScreen(
                product = sampleProducts.first { it.id == productId },
                onBack = { navController.popBackStack() }
            )
        }
    }
}

// Écrans
@Composable
fun HomeScreen(onNavigateToProducts: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Image en arrière-plan, centrée, sans redimensionnement
        Image(
            painter = painterResource(id = R.drawable.homephoto),
            contentDescription = null,
            contentScale = ContentScale.None,
            alignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        )


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Bienvenue",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary // Si image sombre, texte clair
            )
            Spacer(Modifier.height(32.dp))
            Button(onClick = onNavigateToProducts) {
                Text("Voir tous les produits")
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    products: List<Product>,
    onProductClick: (String) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Nos produits",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.Black),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(products) { product ->
                ProductCard(
                    product = product,
                    onClick = { onProductClick(product.id) }
                )
                //Divider(thickness = 1.dp)
            }
        }
    }
}

@Composable
fun ProductCard(product: Product, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .height(230.dp)
            .padding(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            contentColor = Color.Black
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = product.imageP),
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(140.dp)
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                product.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                "$${product.price}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(product: Product, onBack: () -> Unit) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        product.name,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f)
            ) {
                Image(
                    painter = painterResource(id = product.imageP),
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Prix: $${product.price}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Description:",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Black
                )
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp),
                    color = Color.Black
                )
            }
        }
    }
}
