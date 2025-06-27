package com.example.emtyapp.data.repository




import com.example.emtyapp.data.source.LocalProductDataSource
import com.example.emtyapp.data.source.ProductDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindProductDataSource(
        impl: LocalProductDataSource
    ): ProductDataSource

    @Binds
    abstract fun bindProductRepository(
        impl: ProductRepositoryImpl
    ): ProductRepository
}
