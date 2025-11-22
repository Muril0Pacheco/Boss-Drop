package com.example.bossdrop.data.model

data class FavoriteItem(
    val gameTitle: String,
    val gameImageUrl: String?,
    val gamePrice: String,
    val gameDiscount: String? = null,
    val gameId: String
)