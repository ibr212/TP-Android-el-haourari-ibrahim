package com.example.emtyapp.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.emtyapp.mvi.intent.ViewIntent
import com.example.emtyapp.mvi.model.ViewState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    state: ViewState.ProductListState,
    onIntent: (ViewIntent) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Nos produits")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { onIntent(ViewIntent.NavigateBack) }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    IconButton(onClick = { onIntent(ViewIntent.NavigateToCart) }) {
                        Icon(Icons.Filled.ShoppingCart, contentDescription = "Panier")
                    }
                },
                windowInsets = WindowInsets(0.dp)
            )

        },
        contentWindowInsets = WindowInsets(0.dp)
    ) { innerPadding ->

        val paddingWithoutBottom = PaddingValues(
            start = innerPadding.calculateStartPadding(androidx.compose.ui.unit.LayoutDirection.Ltr),
            top = innerPadding.calculateTopPadding(),
            end = innerPadding.calculateEndPadding(androidx.compose.ui.unit.LayoutDirection.Ltr),
            bottom = 0.dp // on supprime le padding bottom (celui de la barre de navigation)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingWithoutBottom)
                .background(MaterialTheme.colorScheme.background)
                .heightIn(max = 600.dp) // limite la hauteur max à 600dp
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.products) { product ->
                    ProductCard(
                        product = product,
                        onClick = {
                            onIntent(ViewIntent.ProductListIntent.ProductSelected(product.id))
                        },
                        onAddClick = {
                            onIntent(ViewIntent.CartIntent.AddToCart(product))
                        }
                    )
                }
            }
        }

        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}
