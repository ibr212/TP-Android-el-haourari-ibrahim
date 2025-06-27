package com.example.emtyapp.mvi.model

import com.example.emtyapp.data.model.CartItem
import com.example.emtyapp.data.model.Product

sealed class ViewState {

    object Loading : ViewState()
    data class Error(val message: String) : ViewState()

    data class HomeState(val isLoading: Boolean = false) : ViewState()

    data class ProductListState(
        val products: List<Product> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val selectedProductId: String? = null      // ← nouveau
    ) : ViewState() {
        companion object {
            fun initial() = ProductListState()     // ← pour l’initialisation du flow
        }
    }

    data class ProductDetailState(
        val product: Product? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    ) : ViewState()

   data class CartState(
        val items: List<CartItem> = emptyList(),
        val totalPrice: Double = 0.0,
        val isLoading: Boolean = false,
        val error: String? = null
    ){
       val itemCount: Int get() = items.size
   }
    fun CartState.calculateTotal(): Double {
        return items.sumOf { it.product.price * it.quantity }
    }

}
