package com.example.emtyapp.data.source

import com.example.emtyapp.R
import com.example.emtyapp.data.model.Product
import kotlinx.coroutines.delay


interface ProductDataSource {
    suspend fun getProducts(): List<Product>
    suspend fun getProductById(id: String): Product?
}