package com.example.emtyapp.data.model

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val cause: Exception? = null) : Result<Nothing>()
    object Loading : Result<Nothing>()
}