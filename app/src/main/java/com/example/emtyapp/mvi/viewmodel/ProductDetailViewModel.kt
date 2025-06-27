package com.example.emtyapp.mvi.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.emtyapp.data.model.Result
import com.example.emtyapp.data.repository.ProductRepository
import com.example.emtyapp.data.repository.ProductRepositoryImpl
import com.example.emtyapp.mvi.intent.ViewIntent
import com.example.emtyapp.mvi.model.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val repository: ProductRepository
) : BaseViewModel<ViewState.ProductDetailState>() {

    private var currentProductId: String? = null

    override fun initialState(): ViewState.ProductDetailState =
        ViewState.ProductDetailState(isLoading = true)

    override fun processIntent(intent: ViewIntent) {
        when (intent) {
            is ViewIntent.ProductDetailIntent.LoadProductDetail -> {
                currentProductId = intent.productId
                loadProductDetail(intent.productId)
            }
            is ViewIntent.ProductDetailIntent.RefreshProductDetail -> {
                currentProductId?.let { loadProductDetail(it) }
            }
            else -> Unit
        }
    }

    private fun loadProductDetail(productId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            repository.getProductById(productId).collectLatest { result ->
                _state.value = when (result) {
                    is Result.Success -> _state.value.copy(
                        product = result.data,
                        isLoading = false,
                        error = null
                    )
                    is Result.Error -> _state.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                    is Result.Loading -> _state.value.copy(isLoading = true)
                }
            }
        }
    }
}
