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


class HomeViewModel : BaseViewModel<ViewState.HomeState>() {
    override fun initialState(): ViewState.HomeState = ViewState.HomeState()

    override fun processIntent(intent: ViewIntent) {
        when (intent) {
            is ViewIntent.HomeIntent.NavigateToProducts -> {
                // Dans une implémentation réelle, vous pourriez avoir une logique ici
                // comme collecter des analytics avant de naviguer
            }
            else -> {} // Ignorer les autres intents
        }
    }
}