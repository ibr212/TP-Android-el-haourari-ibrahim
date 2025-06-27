package com.example.emtyapp.mvi.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.emtyapp.data.model.CartItem
import com.example.emtyapp.data.model.Product
import com.example.emtyapp.mvi.intent.ViewIntent
import com.example.emtyapp.mvi.model.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(ViewState.CartState())
    val state: StateFlow<ViewState.CartState> = _state

    fun processIntent(intent: ViewIntent.CartIntent) {
        viewModelScope.launch {
            when (intent) {
                is ViewIntent.CartIntent.AddToCart -> addToCart(intent.product)
                is ViewIntent.CartIntent.RemoveItem -> removeItem(intent.productId)
                is ViewIntent.CartIntent.UpdateQuantity -> updateQuantity(intent.productId, intent.quantity)
                ViewIntent.CartIntent.ClearCart -> clearCart()
                ViewIntent.CartIntent.Checkout -> processCheckout()
            }
        }
    }

    private fun addToCart(product: Product) {
        val currentItems = _state.value.items.toMutableList()
        val existingItem = currentItems.find { it.product.id == product.id }

        if (existingItem != null) {
            currentItems[currentItems.indexOf(existingItem)] =
                existingItem.copy(quantity = existingItem.quantity + 1)
        } else {
            currentItems.add(CartItem(product, 1))
        }

        updateState(currentItems)
    }

    private fun removeItem(productId: String) {
        val currentItems = _state.value.items.filterNot { it.product.id == productId }
        updateState(currentItems)
    }

    private fun updateQuantity(productId: String, quantity: Int) {
        val currentItems = _state.value.items.toMutableList()
        val itemIndex = currentItems.indexOfFirst { it.product.id == productId }

        if (itemIndex != -1) {
            if (quantity > 0) {
                currentItems[itemIndex] = currentItems[itemIndex].copy(quantity = quantity)
            } else {
                currentItems.removeAt(itemIndex)
            }
            updateState(currentItems)
        }
    }

    private fun clearCart() {
        _state.value = ViewState.CartState()
    }

    private fun processCheckout() {
        Log.d("CartViewModel", "Checkout process started")
        // Ajoutez ici la logique de paiement
    }

    private fun updateState(items: List<CartItem>) {
        _state.value = ViewState.CartState(
            items = items,
            totalPrice = calculateTotal(items),
            isLoading = false
        )
    }

    private fun calculateTotal(items: List<CartItem>): Double {
        return items.sumOf { it.product.price * it.quantity }
    }
}