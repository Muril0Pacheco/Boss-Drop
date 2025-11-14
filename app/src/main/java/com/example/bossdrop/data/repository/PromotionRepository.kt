package com.example.bossdrop.data.repository

import android.util.Log
import com.example.bossdrop.data.model.Deal
import com.example.bossdrop.data.network.RetrofitInstance

/**
 * Repositório responsável por buscar os dados das promoções.
 * Ele "abstrai" a fonte dos dados (seja API, banco local, etc.)
 */
class PromotionRepository {
    /**
     * Busca a lista de promoções ativas da API da CheapShark.
     * Esta é uma função de suspensão e deve ser chamada de uma Coroutine (ex: no ViewModel).
     *
     * @return Uma lista de [Deal] (promoções) ou uma lista vazia se um erro ocorrer.
     */
    suspend fun getPromotionsFromApi(): List<Deal> {
        return try {
            // Chama a função da nossa interface de API
            // (sortBy e onSale já têm valores padrão na interface)
            val dealList = RetrofitInstance.api.getDeals()
            dealList // Retorna a lista de promoções se a chamada for bem-sucedida

        } catch (e: Exception) {
            // Se der qualquer erro (ex: sem internet, API fora do ar)
            // nós logamos o erro e retornamos uma lista vazia.
            Log.e("PromotionRepository", "Erro ao buscar promoções da API: ${e.message}")
            emptyList() // Retorna uma lista vazia para o app não quebrar
        }
    }
}