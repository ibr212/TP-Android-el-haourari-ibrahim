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
import com.example.emtyapp.data.Api.ProductApi
import android.util.Log
import java.io.Console
import java.util.concurrent.TimeUnit
import javax.inject.Inject
class ProductRepositoryImpl @Inject constructor(
    private val localDataSource: ProductDataSource,
    private val remoteDataSource: RemoteProductDataSource
) : ProductRepository {

    private var cachedProducts: List<Product>? = null
    private var lastFetchTime: Long = 0
    private val CACHE_DURATION = TimeUnit.MINUTES.toMillis(5) // 5 minutes cache

    override suspend fun getProducts(): Flow<Result<List<Product>>> = flow {
        emit(Result.Loading)

        try {
            // Vérifier si le cache est valide
            val shouldFetchFromNetwork = cachedProducts == null ||
                    (System.currentTimeMillis() - lastFetchTime) > CACHE_DURATION.toLong()

            if (!shouldFetchFromNetwork) {
                cachedProducts?.let {
                    emit(Result.Success(it))
                    return@flow
                }
            }

            // Essayer le réseau d'abord (stratégie réseau-premier)
            val remoteProducts = try {
                remoteDataSource.getProducts().also {
                    // Mettre à jour le cache et la source locale
                    cachedProducts = it
                    lastFetchTime = System.currentTimeMillis()
                    //localDataSource.saveProducts(it)
                }
            } catch (e: Exception) {
                Log.w("ProductRepo", "Network failed, falling back to local", e)
                null
            }

            // Si le réseau a réussi, émettre les résultats
            remoteProducts?.let {
                emit(Result.Success(it))
                return@flow
            }

            // Fallback local
            val localProducts = localDataSource.getProducts()
            if (localProducts.isNotEmpty()) {
                cachedProducts = localProducts
                emit(Result.Success(localProducts))
            } else {
                emit(Result.Error("Aucun produit disponible"))
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
