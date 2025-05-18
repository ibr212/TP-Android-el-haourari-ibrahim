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
class ProductListViewModel(
    private val repository: ProductRepository = ProductRepositoryImpl()
) : BaseViewModel<ViewState.ProductListState>() {
    override fun initialState(): ViewState.ProductListState =
        ViewState.ProductListState(isLoading = true)

    override fun processIntent(intent: ViewIntent) {
        when (intent) {
            is ViewIntent.ProductListIntent.LoadProducts -> loadProducts()
            is ViewIntent.ProductListIntent.RefreshProducts -> refreshProducts()
            is ViewIntent.ProductListIntent.ProductSelected -> {
                // Logique pour la sélection d'un produit (analytics, etc.)
            }

            else -> {} // Ignorer les autres intents
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            repository.getProducts().collectLatest { result ->
                when (result) {
                    is Result.Success -> {
                        _state.value = ViewState.ProductListState(
                            products = result.data,
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

    private fun refreshProducts() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            when (val refreshResult = repository.refreshProducts()) {
                is Result.Success -> loadProducts()
                is Result.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = refreshResult.message
                    )
                }

                is Result.Loading -> {
                    // Rien à faire ici
                }
            }
        }
    }

    // Factory pour injecter le repository
    class Factory(private val repository: ProductRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProductListViewModel::class.java)) {
                return ProductListViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}