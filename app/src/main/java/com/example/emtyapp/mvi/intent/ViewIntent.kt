package com.example.emtyapp.mvi.intent

import com.example.emtyapp.data.model.Product

sealed class ViewIntent {
    // Actions globales
    object Initialize : ViewIntent()
    object NavigateBack : ViewIntent()
    object NavigateToCart : ViewIntent()

    // Actions spécifiques à chaque écran
    sealed class HomeIntent : ViewIntent() {
        object NavigateToProducts : HomeIntent()
    }

    sealed class ProductListIntent : ViewIntent() {
        object LoadProducts : ProductListIntent()
        object RefreshProducts : ProductListIntent()
        data class ProductSelected(val productId: String) : ProductListIntent()
    }

    sealed class ProductDetailIntent : ViewIntent() {
        data class LoadProductDetail(val productId: String) : ProductDetailIntent()
        object RefreshProductDetail : ProductDetailIntent()
    }

    sealed class CartIntent : ViewIntent() {
        data class AddToCart(val product: Product) : CartIntent()
        //data class RemoveFromCart(val productId: String) : CartIntent()
        data class RemoveItem(val productId: String) : CartIntent()
        data class UpdateQuantity(val productId: String, val quantity: Int) : CartIntent()
        object ClearCart : CartIntent()
        object Checkout : CartIntent()
    }
    sealed class AuthIntent : ViewIntent() {
        // Login
        data class UpdateLoginEmail(val email: String) : AuthIntent()
        data class UpdateLoginPassword(val password: String) : AuthIntent()
        object Login : AuthIntent()

        // Register
        data class UpdateRegisterName(val name: String) : AuthIntent()
        data class UpdateRegisterEmail(val email: String) : AuthIntent()
        data class UpdateRegisterPassword(val password: String) : AuthIntent()
        data class UpdateConfirmPassword(val confirmPassword: String) : AuthIntent()
        object Register : AuthIntent()

        // Common
        object Logout : AuthIntent()
        object ClearError : AuthIntent()
        object CheckAuthStatus : AuthIntent()
    }


}
