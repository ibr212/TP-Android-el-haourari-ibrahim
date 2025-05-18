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

// ViewModel de base avec fonctionnalité MVI
abstract class BaseViewModel<S : ViewState> : ViewModel() {
    protected val _state = MutableStateFlow<S>(initialState())
    val state: StateFlow<S> = _state.asStateFlow()

    abstract fun initialState(): S
    abstract fun processIntent(intent: ViewIntent)
}