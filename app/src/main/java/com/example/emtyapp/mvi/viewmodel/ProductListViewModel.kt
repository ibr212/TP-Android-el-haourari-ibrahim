package com.example.emtyapp.mvi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider          // ← import ajouté
import androidx.lifecycle.viewModelScope
import com.example.emtyapp.data.model.Result
import com.example.emtyapp.data.repository.ProductRepository
import com.example.emtyapp.mvi.intent.ViewIntent.ProductListIntent
import com.example.emtyapp.mvi.intent.ViewIntent
import com.example.emtyapp.mvi.model.ViewState.ProductListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    // ----- STATE -----
    private val _state = MutableStateFlow<ProductListState>(ProductListState.initial())
    val state: StateFlow<ProductListState> = _state

    // ----- INTENTS -----
    fun processIntent(intent: ViewIntent) {
        when (intent) {
            is ViewIntent.ProductListIntent.LoadProducts -> loadProducts()
            is ViewIntent.ProductListIntent.RefreshProducts -> refreshProducts()
            is ViewIntent.ProductListIntent.ProductSelected -> handleProductSelected(intent.productId)
            is ViewIntent.NavigateBack -> {
                // Optionnel : gérer la navigation ici, ou ignorer
            }
            else -> {
                // Ignorer les autres intents
            }
        }
    }


    // ----- ACTIONS -----
    private fun loadProducts() {
        viewModelScope.launch {
            repository.getProducts().collectLatest { result ->
                when (result) {
                    is Result.Loading -> {
                        _state.value = _state.value.copy(isLoading = true)
                    }
                    is Result.Success -> {
                        _state.value = _state.value.copy(
                            products   = result.data,
                            isLoading  = false,
                            error      = null
                        )
                    }
                    is Result.Error -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error     = result.message
                        )
                    }
                }
            }
        }
    }

    private fun refreshProducts() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            when (val res = repository.refreshProducts()) {
                is Result.Success -> loadProducts()
                is Result.Error   -> _state.value = _state.value.copy(
                    isLoading = false,
                    error = res.message
                )
                else -> {
                    // Rien à faire ici
                }
            }

        }
    }

    /** Mise à jour de l’état quand l’utilisateur sélectionne un produit. */
    private fun handleProductSelected(productId: String) {
        _state.value = _state.value.copy(selectedProductId = productId)
        // Ici : déclenche ta navigation ou autre logique si besoin
    }

    // ----- Factory (optionnelle si Hilt fournit tout) -----
    class Factory(private val repository: ProductRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            if (modelClass.isAssignableFrom(ProductListViewModel::class.java)) {
                ProductListViewModel(repository) as T
            } else {
                throw IllegalArgumentException("Unknown ViewModel class")
            }
    }
}
