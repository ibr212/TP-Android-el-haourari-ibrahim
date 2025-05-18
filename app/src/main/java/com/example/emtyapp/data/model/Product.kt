package com.example.emtyapp.data.model

data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val description: String,
    val imageUrl: String? = null,
    val imageResourceId: Int? = null
)