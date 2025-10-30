package com.example.bossdrop.data.model

data class Promotion(
    val gameTitle: String,
    val discount: String,
    val oldPrice: String,
    val newPrice: String,
    val gameImageResId: Int,
    val storeLogoResId: Int
)