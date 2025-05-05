package com.example.emtyapp.data

data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val description: String,
    val imageUrl: String
)

val sampleProducts = listOf(
    Product("1", "Smartphone", 599.99, "Dernier modèle avec caméra haute résolution", "url_image1"),
    Product("2", "Ordinateur", 999.99, "PC portable 16GB RAM", "url_image2"),
    Product("3", "Casque", 199.99, "Réduction de bruit active", "url_image3")
)