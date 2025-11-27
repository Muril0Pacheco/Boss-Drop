package com.murilo.bossdrop.data.repository

import android.util.Log
import com.murilo.bossdrop.data.model.ItadPromotion
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PromotionRepository(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    suspend fun getPromotionsFromFirestore(): List<ItadPromotion> {
        return try {
            val snapshot = db.collection("promocoes_br_v3").get().await()

            val promotionList = snapshot.toObjects(ItadPromotion::class.java)

            Log.d("PromotionRepository", "Sucesso! ${promotionList.size} promoções carregadas.")
            promotionList

        } catch (e: Exception) {
            Log.e("PromotionRepository", "Erro ao buscar promoções do Firestore: ${e.message}")
            emptyList()
        }
    }

    suspend fun getPromotionById(gameId: String): ItadPromotion? {
        return try {
            val document = db.collection("promocoes_br_v3").document(gameId).get().await()
            val promotion = document.toObject(ItadPromotion::class.java)

            if (promotion != null) {
                Log.d("PromotionRepository", "Sucesso! Carregou: ${promotion.title}")
            } else {
                Log.w("PromotionRepository", "Jogo com ID $gameId não encontrado.")
            }
            promotion

        } catch (e: Exception) {
            Log.e("PromotionRepository", "Erro ao buscar promoção por ID: ${e.message}")
            null
        }
    }
}