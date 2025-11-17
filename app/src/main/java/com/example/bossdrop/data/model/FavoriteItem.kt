package com.example.bossdrop.data.model

data class FavoriteItem(
    val gameTitle: String,
    val gameImageResId: Int,
    val gamePrice: String,
    val gameDiscount: String? = null
)