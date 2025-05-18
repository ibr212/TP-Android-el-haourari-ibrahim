package com.example.emtyapp.mvi.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.emtyapp.data.model.Result
import com.example.emtyapp.data.repository.ProductRepository
import com.example.emtyapp.data.repository.ProductRepositoryImpl
import com.example.emtyapp.mvi.intent.ViewIntent
import com.example.emtyapp.mvi.model.ViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProductDetailViewModel(
    private val repository: ProductRepository = ProductRepositoryImpl()
) : BaseViewModel<ViewState.ProductDetailState>() {
    private var currentProductId: String? = null

    override fun initialState(): ViewState.ProductDetailState = ViewState.ProductDetailState(isLoading = true)

    override fun processIntent(intent: ViewIntent) {
        when (intent) {
            is ViewIntent.ProductDetailIntent.LoadProductDetail -> {
                currentProductId = intent.productId
                loadProductDetail(intent.productId)
            }
            is ViewIntent.ProductDetailIntent.RefreshProductDetail -> {
                currentProductId?.let { loadProductDetail(it) }
            }
            else -> {} // Ignorer les autres intents
        }
    }

    private fun loadProductDetail(productId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            repository.getProductById(productId).collectLatest { result ->
                when (result) {
                    is Result.Success -> {
                        _state.value = ViewState.ProductDetailState(
                            product = result.data,
                            isLoading = false,
                            error = null
                        )
                    }
                    is Result.Error -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                    is Result.Loading -> {
                        _state.value = _state.value.copy(isLoading = true)
                    }
                }
            }
        }
    }

    // Factory pour injecter le repository
    class Factory(private val repository: ProductRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProductDetailViewModel::class.java)) {
                return ProductDetailViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}