package com.example.emtyapp.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.emtyapp.data.model.CartItem
import com.example.emtyapp.data.model.Order
import com.example.emtyapp.data.model.Product

import com.example.emtyapp.mvi.intent.ViewIntent
import com.example.emtyapp.mvi.model.ViewState
import com.example.emtyapp.nav.Routes
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    state: ViewState.CartState,
    onIntent: (ViewIntent.CartIntent) -> Unit,
    navController: NavController,
    onOrderCreated: (Order) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Mon Panier")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                windowInsets = WindowInsets(0.dp)
            )
        },
        contentWindowInsets = WindowInsets(0.dp)
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            color = MaterialTheme.colorScheme.background){
            if (state.items.isEmpty()) {
                EmptyCartView()
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(state.items) { item ->
                            CartItemCard(
                                item = item,
                                onQuantityChange = { newQuantity ->
                                    onIntent(ViewIntent.CartIntent.UpdateQuantity(item.product.id, newQuantity))
                                },
                                onRemove = { onIntent(ViewIntent.CartIntent.RemoveItem(item.product.id)) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    CartSummarySection(
                        totalPrice = state.totalPrice,
                        itemCount = state.items.size,
                        items = state.items,
                        onClearCart = { onIntent(ViewIntent.CartIntent.ClearCart) },
                        navController = navController,
                        onOrderCreated = onOrderCreated
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductImage(
    product: Product,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp)
    ) {
        when {
            !product.imageUrl.isNullOrEmpty() -> {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(product.imageUrl)
                        .build(),
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            product.imageResourceId != null -> {
                Image(
                    painter = painterResource(product.imageResourceId),
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyCartView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.ShoppingCart,
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Votre panier est vide",
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

@Composable
private fun CartItemCard(
    item: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProductImage(
                product = item.product,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.product.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "€${"%.2f".format(item.product.price)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    QuantitySelector(
                        quantity = item.quantity,
                        onDecrease = { onQuantityChange(item.quantity - 1) },
                        onIncrease = { onQuantityChange(item.quantity + 1) }
                    )
                    IconButton(onClick = onRemove) {
                        Icon(Icons.Default.Delete, contentDescription = "Supprimer")
                    }
                }
            }
        }
    }
}

@Composable
private fun QuantitySelector(
    quantity: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onDecrease,
            enabled = quantity > 1
        ) {
            Icon(Icons.Default.Remove, contentDescription = "Diminuer")
        }
        Text(
            text = quantity.toString(),
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        IconButton(onClick = onIncrease) {
            Icon(Icons.Default.Add, contentDescription = "Augmenter")
        }
    }
}

@Composable
private fun CartSummarySection(
    totalPrice: Double,
    itemCount: Int,
    items: List<CartItem>,
    onClearCart: () -> Unit,
    navController: NavController,
    onOrderCreated: (Order) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Total ($itemCount articles)")
            Text("€${"%.2f".format(totalPrice)}", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onClearCart,
                modifier = Modifier.weight(1f)
            ) {
                Text("Vider")
            }
            Button(
                onClick = {
                    val newOrder = Order(
                        id = UUID.randomUUID().toString(),
                        items = items,
                        totalPrice = totalPrice,
                        date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                    )

                    // 🟢 Notifier la création de la commande
                    onOrderCreated(newOrder)

                    // Sauvegarder pour OrderScreen
                    navController.currentBackStackEntry?.savedStateHandle?.set("order", newOrder)

                    // Effacer le panier
                    onClearCart()

                    // Naviguer vers OrderScreen
                    navController.navigate(Routes.ORDER)

                    Log.d("CART", "Commande créée: ${newOrder.id}")
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Commander")
            }
        }
    }
}
