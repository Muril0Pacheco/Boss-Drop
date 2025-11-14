package com.example.bossdrop.data.model

import com.google.gson.annotations.SerializedName

/**
 * Representa uma única promoção (Deal) vinda da API da CheapShark.
 * Os nomes das variáveis aqui correspondem exatamente aos campos do JSON.
 */
data class Deal(
    // Campos principais que vamos usar
    val title: String,
    val salePrice: String,
    val normalPrice: String,
    val savings: String,         // O percentual de desconto (ex: "85.042521")
    val thumb: String,           // A URL da imagem da capa (ex: "https://cdn...")
    val dealID: String,          // O ID para o link de redirecionamento
    val storeID: String,

    // Campos extras que são úteis
    val gameID: String,
    val steamAppID: String?,     // Pode ser nulo, então usamos '?'
    val metacriticScore: String,
    val dealRating: String       // A "nota" da promoção
)