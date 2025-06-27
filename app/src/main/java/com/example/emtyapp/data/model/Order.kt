package com.example.emtyapp.data.model
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    val id: String,
    val items: List<CartItem>,
    val totalPrice: Double,
    val date: String,
    val status: String = "En cours"
) : Parcelable
