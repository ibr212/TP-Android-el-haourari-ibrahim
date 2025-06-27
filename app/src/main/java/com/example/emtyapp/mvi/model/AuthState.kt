package com.example.emtyapp.mvi.model

import com.example.emtyapp.data.model.User

data class AuthState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val isLoggedIn: Boolean = false,
    val error: String? = null,
    val loginEmail: String = "",
    val loginPassword: String = "",
    val registerName: String = "",
    val registerEmail: String = "",
    val registerPassword: String = "",
    val confirmPassword: String = ""
)