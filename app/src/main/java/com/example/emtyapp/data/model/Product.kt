package com.example.emtyapp.data.model
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val description: String,
    val imageUrl: String? = null,
    val imageResourceId: Int? = null
) : Parcelable