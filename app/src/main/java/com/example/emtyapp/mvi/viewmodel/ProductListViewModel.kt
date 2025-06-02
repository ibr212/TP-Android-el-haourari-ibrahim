package com.example.emtyapp.mvi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.emtyapp.data.model.Result
import com.example.emtyapp.data.repository.ProductRepository
import com.example.emtyapp.mvi.intent.ProductListIntent
import com.example.emtyapp.mvi.model.ProductListState
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

    // State
    private val _state = MutableStateFlow(ProductListState.initial())
    val state: StateFlow<ProductListState> = _state

    // Intent processor
    fun processIntent(intent: ProductListIntent) {
        when (intent) {
            is ProductListIntent.LoadProducts -> loadProducts()
            is ProductListIntent.RefreshProducts -> refreshProducts()
            is ProductListIntent.ProductSelected -> handleProductSelected(intent.productId)
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            repository.getProducts().collectLatest { result ->
                when (result) {
                    is Result.Success -> {
                        _state.value = ProductListState(
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
                    // No action needed
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