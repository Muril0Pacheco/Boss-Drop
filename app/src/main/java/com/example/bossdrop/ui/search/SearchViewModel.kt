package com.example.bossdrop.ui.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bossdrop.data.model.ItadPromotion // ◀️ --- IMPORT MUDOU
import com.example.bossdrop.data.repository.PromotionRepository
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    private val repository = PromotionRepository()

    // Lista "Mais Buscados" (continua estática)
    private val _searchHistory = MutableLiveData<List<String>>()
    val searchHistory: LiveData<List<String>> = _searchHistory

    // ◀️ --- ALTERADO ---
    // Esta lista guardará os "Recomendados" OU os "Resultados de Busca"
    private val _dealList = MutableLiveData<List<ItadPromotion>>()
    val dealList: LiveData<List<ItadPromotion>> = _dealList

    // Controla se mostramos "Recomendados" ou "Resultados da Busca"
    private val _isSearchMode = MutableLiveData<Boolean>(false)
    val isSearchMode: LiveData<Boolean> = _isSearchMode

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadData() // Carrega "Mais Buscados" e "Recomendados"
    }

    private fun loadData() {
        // Carrega a lista estática de "Mais Buscados"
        _searchHistory.value = listOf("Cyberpunk 2077", "Elden Ring", "Hollow Knight")

        // Inicia a chamada ao Firestore para os "Recomendados"
        viewModelScope.launch {
            try {
                _isLoading.value = true

                // 1. Busca TODAS as 200 promoções do Firestore
                val allDeals = repository.getPromotionsFromFirestore()

                // 2. CRIA A LISTA DE "RECOMENDADOS"
                // A Home já mostra por desconto. Vamos ordenar por PREÇO MAIS BAIXO.
                val recommendedList = allDeals.sortedBy { it.deal?.price?.amount ?: Double.MAX_VALUE }

                _dealList.value = recommendedList

            } catch (e: Exception) {
                Log.e("SearchViewModel", "Erro ao carregar recomendados: ${e.message}")
                _dealList.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Busca jogos no Firestore com base no texto do usuário.
     */
    fun searchForGame(query: String) {
        if (query.isBlank()) {
            return
        }

        _isSearchMode.value = true

        viewModelScope.launch {
            try {
                _isLoading.value = true

                // 1. Pega todas as promoções
                val allDeals = repository.getPromotionsFromFirestore()

                // 2. Filtra a lista AQUI NO APP
                val searchResults = allDeals.filter {
                    // Procura no título, ignorando maiúsculas/minúsculas
                    it.title.contains(query, ignoreCase = true)
                }

                _dealList.value = searchResults

            } catch (e: Exception) {
                Log.e("SearchViewModel", "Erro ao buscar por título: ${e.message}")
                _dealList.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}