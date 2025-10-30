package com.example.bossdrop.data.repository

import com.example.bossdrop.data.model.Promotion

class PromotionRepository {

    // Simula dados locais por enquanto
    fun getPromotions(): List<Promotion> {
        return listOf(
            Promotion(
                gameTitle = "GTA V",
                discount = "-60%",
                oldPrice = "R$199,99",
                newPrice = "R$79,99",
                gameImageResId = com.example.bossdrop.R.drawable.gta_cover,
                storeLogoResId = com.example.bossdrop.R.drawable.steam_logo
            )
        )
    }

    // Futuramente aqui:
    // fun getPromotionsFromApi(): List<Promotion> {
    //     // Chamada HTTP ou leitura de arquivo JSON gerado pelo Python
    // }
}
