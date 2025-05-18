package com.example.emtyapp.data.repository
import com.example.emtyapp.data.model.Product
import com.example.emtyapp.data.model.Result
import com.example.emtyapp.data.source.LocalProductDataSource
import com.example.emtyapp.data.source.ProductDataSource
import com.example.emtyapp.data.source.RemoteProductDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

interface ProductRepository {
    suspend fun getProducts(): Flow<Result<List<Product>>>
    suspend fun getProductById(id: String): Flow<Result<Product>>
    suspend fun refreshProducts(): Result<Unit>
}