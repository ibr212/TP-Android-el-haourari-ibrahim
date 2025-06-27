package com.example.emtyapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.emtyapp.data.model.Order

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderListScreen(
    orders: List<Order>,
    onOrderClick: (Order) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historique des commandes") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(orders) { order ->
                ListItem(
                    headlineContent = { Text("Commande ${order.id.take(8)}") },
                    supportingContent = { Text("Date : ${order.date} - Total : €${"%.2f".format(order.totalPrice)}") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOrderClick(order) }
                        .padding(vertical = 8.dp, horizontal = 4.dp)
                )
                Divider()
            }
        }
    }
}
