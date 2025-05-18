package com.example.emtyapp.mvi.model



import com.example.emtyapp.data.model.Product

// État générique pour tous les écrans
sealed class ViewState {
    // État initial avec chargement
    object Loading : ViewState()

    // État d'erreur
    data class Error(val message: String) : ViewState()

    // États spécifiques pour chaque écran
    data class HomeState(val isLoading: Boolean = false) : ViewState()
    data class ProductListState(
        val products: List<Product> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    ) : ViewState()
    data class ProductDetailState(
        val product: Product? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    ) : ViewState()
}
