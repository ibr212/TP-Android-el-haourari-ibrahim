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


class ProductRepositoryImpl(
    private val localDataSource: ProductDataSource = LocalProductDataSource(),
    private val remoteDataSource: ProductDataSource = RemoteProductDataSource()
) : ProductRepository {

    // Cache en mémoire pour optimiser les performances
    private var cachedProducts: List<Product>? = null

    override suspend fun getProducts(): Flow<Result<List<Product>>> = flow {
        emit(Result.Loading)

        try {
            // Essayons d'abord d'utiliser le cache
            cachedProducts?.let {
                emit(Result.Success(it))
                return@flow
            }

            // Sinon, charger depuis la source locale
            val localProducts = localDataSource.getProducts()
            if (localProducts.isNotEmpty()) {
                cachedProducts = localProducts
                emit(Result.Success(localProducts))
            } else {
                // Si vide, charger depuis le réseau
                val remoteProducts = remoteDataSource.getProducts()
                cachedProducts = remoteProducts
                emit(Result.Success(remoteProducts))
            }
        } catch (e: Exception) {
            emit(Result.Error("Erreur lors du chargement des produits", e))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getProductById(id: String): Flow<Result<Product>> = flow {
        emit(Result.Loading)

        try {
            // Essayer d'abord le cache
            cachedProducts?.find { it.id == id }?.let {
                emit(Result.Success(it))
                return@flow
            }

            // Essayer la source locale
            val localProduct = localDataSource.getProductById(id)
            if (localProduct != null) {
                emit(Result.Success(localProduct))
            } else {
                // Essayer la source distante
                val remoteProduct = remoteDataSource.getProductById(id)
                if (remoteProduct != null) {
                    emit(Result.Success(remoteProduct))
                } else {
                    emit(Result.Error("Produit non trouvé"))
                }
            }
        } catch (e: Exception) {
            emit(Result.Error("Erreur lors du chargement du produit", e))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun refreshProducts(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val remoteProducts = remoteDataSource.getProducts()
            cachedProducts = remoteProducts
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Impossible de rafraîchir les produits", e)
        }
    }
}
