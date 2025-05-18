package com.example.emtyapp.mvi.intent

sealed class ViewIntent {
    // Actions globales
    object Initialize : ViewIntent()
    object NavigateBack : ViewIntent()

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
}
