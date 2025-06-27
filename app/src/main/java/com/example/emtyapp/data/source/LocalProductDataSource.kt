package com.example.emtyapp.data.source

import com.example.emtyapp.R
import com.example.emtyapp.data.model.Product

import kotlinx.coroutines.delay
import javax.inject.Inject

class LocalProductDataSource @Inject constructor() : ProductDataSource {
    private val products = listOf(
        Product("1", "Smartphone", 599.99, "Écran 6.5\", 128GB Stockage", imageResourceId = R.drawable.smartphone),
        Product("2", "Ordinateur", 1299.99, "16GB RAM, SSD 512GB", imageResourceId = R.drawable.ordinateur),
        Product("3", "Casque Audio", 249.99, "Réduction de bruit active", imageResourceId = R.drawable.casque),
        Product("4", "Smartphone", 599.99, "Écran 6.5\", 128GB Stockage", imageResourceId = R.drawable.smartphone),
        Product("5", "Ordinateur", 1299.99, "16GB RAM, SSD 512GB", imageResourceId = R.drawable.ordinateur),
        Product("6", "Casque Audio", 249.99, "Réduction de bruit active", imageResourceId = R.drawable.casque),
        Product("7", "Smartphone", 599.99, "Écran 6.5\", 128GB Stockage", imageResourceId = R.drawable.smartphone),
        Product("8", "Ordinateur", 1299.99, "16GB RAM, SSD 512GB", imageResourceId = R.drawable.ordinateur),
        Product("9", "Casque Audio", 249.99, "Réduction de bruit active", imageResourceId = R.drawable.casque),
        Product("10", "Smartphone", 599.99, "Écran 6.5\", 128GB Stockage", imageResourceId = R.drawable.smartphone),
        Product("11", "Ordinateur", 1299.99, "16GB RAM, SSD 512GB", imageResourceId = R.drawable.ordinateur),
        Product("12", "Casque Audio", 249.99, "Réduction de bruit active", imageResourceId = R.drawable.casque)
    )

    override suspend fun getProducts(): List<Product> {
        // Simulation d'un délai réseau
        delay(500)
        return products
    }

    override suspend fun getProductById(id: String): Product? {
        // Simulation d'un délai réseau
        delay(300)
        return products.find { it.id == id }
    }
}
