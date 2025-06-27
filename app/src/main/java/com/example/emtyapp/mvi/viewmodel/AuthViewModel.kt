package com.example.emtyapp.mvi.viewmodel



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.emtyapp.data.repository.AuthRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.emtyapp.mvi.model.AuthState
import com.example.emtyapp.mvi.intent.ViewIntent.AuthIntent

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    init {
        checkAuthStatus()
    }

    fun processIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.UpdateLoginEmail -> updateLoginEmail(intent.email)
            is AuthIntent.UpdateLoginPassword -> updateLoginPassword(intent.password)
            is AuthIntent.Login -> login()

            is AuthIntent.UpdateRegisterName -> updateRegisterName(intent.name)
            is AuthIntent.UpdateRegisterEmail -> updateRegisterEmail(intent.email)
            is AuthIntent.UpdateRegisterPassword -> updateRegisterPassword(intent.password)
            is AuthIntent.UpdateConfirmPassword -> updateConfirmPassword(intent.confirmPassword)
            is AuthIntent.Register -> register()

            is AuthIntent.Logout -> logout()
            is AuthIntent.ClearError -> clearError()
            is AuthIntent.CheckAuthStatus -> checkAuthStatus()
        }
    }

    private fun updateLoginEmail(email: String) {
        _state.value = _state.value.copy(loginEmail = email, error = null)
    }

    private fun updateLoginPassword(password: String) {
        _state.value = _state.value.copy(loginPassword = password, error = null)
    }

    private fun updateRegisterName(name: String) {
        _state.value = _state.value.copy(registerName = name, error = null)
    }

    private fun updateRegisterEmail(email: String) {
        _state.value = _state.value.copy(registerEmail = email, error = null)
    }

    private fun updateRegisterPassword(password: String) {
        _state.value = _state.value.copy(registerPassword = password, error = null)
    }

    private fun updateConfirmPassword(confirmPassword: String) {
        _state.value = _state.value.copy(confirmPassword = confirmPassword, error = null)
    }

    private fun login() {
        val currentState = _state.value

        if (currentState.loginEmail.isBlank() || currentState.loginPassword.isBlank()) {
            _state.value = currentState.copy(error = "Veuillez remplir tous les champs")
            return
        }

        _state.value = currentState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            authRepository.login(currentState.loginEmail, currentState.loginPassword)
                .onSuccess { user ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        user = user,
                        isLoggedIn = true,
                        loginEmail = "",
                        loginPassword = ""
                    )
                }
                .onFailure { exception ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = exception.message
                    )
                }
        }
    }

    private fun register() {
        val currentState = _state.value

        if (currentState.registerName.isBlank() ||
            currentState.registerEmail.isBlank() ||
            currentState.registerPassword.isBlank()) {
            _state.value = currentState.copy(error = "Veuillez remplir tous les champs")
            return
        }

        if (currentState.registerPassword != currentState.confirmPassword) {
            _state.value = currentState.copy(error = "Les mots de passe ne correspondent pas")
            return
        }

        if (currentState.registerPassword.length < 6) {
            _state.value = currentState.copy(error = "Le mot de passe doit contenir au moins 6 caractères")
            return
        }

        _state.value = currentState.copy(isLoading = true, error = null)

        viewModelScope.launch {
            authRepository.register(
                currentState.registerName,
                currentState.registerEmail,
                currentState.registerPassword
            )
                .onSuccess { user ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        user = user,
                        isLoggedIn = true,
                        registerName = "",
                        registerEmail = "",
                        registerPassword = "",
                        confirmPassword = ""
                    )
                }
                .onFailure { exception ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = exception.message
                    )
                }
        }
    }

    private fun logout() {
        authRepository.logout()
        _state.value = AuthState()
    }

    private fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    private fun checkAuthStatus() {
        val user = authRepository.getCurrentUser()
        _state.value = _state.value.copy(
            user = user,
            isLoggedIn = user != null
        )
    }
}