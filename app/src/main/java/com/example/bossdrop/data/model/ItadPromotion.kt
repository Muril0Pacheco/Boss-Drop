package com.example.bossdrop.data.model

import com.google.firebase.firestore.PropertyName

/**
 * ESTE É O NOSSO MODELO V3 ATUALIZADO.
 * Ele representa um documento da coleção "promocoes_br_v3".
 *
 * Importante: O Firestore (com KTX) precisa de um construtor vazio,
 * por isso damos valores padrão (como = "" ou = null) para tudo.
 */
data class ItadPromotion(
    @get:PropertyName("id") val id: String = "",
    @get:PropertyName("title") val title: String = "",
    @get:PropertyName("slug") val slug: String = "",
    @get:PropertyName("assets") val assets: ItadAssets? = null,
    @get:PropertyName("deal") val deal: ItadDealInfo? = null
)

data class ItadAssets(
    @get:PropertyName("boxart") val boxart: String? = null,
    @get:PropertyName("banner600") val banner600: String? = null
)

data class ItadDealInfo(
    @get:PropertyName("shop") val shop: ItadShop? = null,
    @get:PropertyName("price") val price: ItadPrice? = null,
    @get:PropertyName("regular") val regular: ItadPrice? = null,
    @get:PropertyName("cut") val cut: Int = 0,
    @get:PropertyName("url") val url: String = ""
)

data class ItadShop(
    @get:PropertyName("id") val id: Int = 0,
    @get:PropertyName("name") val name: String = ""
)

data class ItadPrice(
    @get:PropertyName("amount") val amount: Double = 0.0,
    @get:PropertyName("currency") val currency: String = ""
)