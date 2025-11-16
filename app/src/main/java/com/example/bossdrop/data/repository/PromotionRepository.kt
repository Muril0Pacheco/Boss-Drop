package com.example.bossdrop.data.repository

import android.util.Log
import com.example.bossdrop.data.model.ItadPromotion
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlin.text.get

/**
 * Repositório responsável por buscar os dados das promoções.
 * Agora ele lê do Firestore, não mais do Retrofit/CheapShark.
 */
class PromotionRepository {

    // Pega a instância do Firestore
    private val db = FirebaseFirestore.getInstance()

    /**
     * Busca a lista de promoções da coleção 'promocoes_br_v3' no Firestore.
     * Esta é uma função de suspensão e deve ser chamada de uma Coroutine (ex: no ViewModel).
     *
     * @return Uma lista de [ItadPromotion] ou uma lista vazia se um erro ocorrer.
     */
    suspend fun getPromotionsFromFirestore(): List<ItadPromotion> {
        return try {
            // Chama o Firestore, busca a coleção e espera o resultado
            val snapshot = db.collection("promocoes_br_v3").get().await()

            // Converte cada documento do Firestore automaticamente
            // para o nosso 'molde' ItadPromotion.
            val promotionList = snapshot.toObjects(ItadPromotion::class.java)

            Log.d("PromotionRepository", "Sucesso! ${promotionList.size} promoções carregadas.")
            promotionList // Retorna a lista

        } catch (e: Exception) {
            // Se der qualquer erro (ex: sem internet, permissão negada)
            Log.e("PromotionRepository", "Erro ao buscar promoções do Firestore: ${e.message}")
            emptyList() // Retorna uma lista vazia para o app não quebrar
        }
    }
    /**
     * Busca os detalhes de UMA promoção específica pelo seu ID.
     */
    suspend fun getPromotionById(gameId: String): ItadPromotion? {
        return try {
            val document = db.collection("promocoes_br_v3").document(gameId).get().await()
            val promotion = document.toObject(ItadPromotion::class.java)

            if (promotion != null) {
                Log.d("PromotionRepository", "Sucesso! Carregou: ${promotion.title}")
            } else {
                Log.w("PromotionRepository", "Jogo com ID $gameId não encontrado.")
            }
            promotion // Retorna a promoção (ou null se não for encontrada)

        } catch (e: Exception) {
            Log.e("PromotionRepository", "Erro ao buscar promoção por ID: ${e.message}")
            null // Retorna nulo em caso de erro
        }
    }
}