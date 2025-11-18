package com.example.bossdrop.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bossdrop.data.model.ItadPromotion
import com.example.bossdrop.data.repository.PromotionRepository
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val repository = PromotionRepository()

    private var fullList: List<ItadPromotion> = emptyList()

    private val _promotions = MutableLiveData<List<ItadPromotion>>()
    val promotions: LiveData<List<ItadPromotion>> = _promotions

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    enum class FilterType {
        RECENT,
        LOWEST_PRICE,
        HIGHEST_DISCOUNT,
        MOST_POPULAR
    }

    init {
        loadPromotions(initialFilter = FilterType.MOST_POPULAR)
    }

    /**
     * Busca as promoções do Firestore e armazena na lista de cache (fullList).

     */
    fun loadPromotions(initialFilter: FilterType = FilterType.RECENT) { // <-- Parâmetro reintroduzido
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val promotionList = repository.getPromotionsFromFirestore()

                fullList = promotionList

                applyFilter(initialFilter) // <-- Agora 'initialFilter' está resolvido


            } catch (e: Exception) {
                Log.e("HomeViewModel", "Erro ao carregar promoções: ${e.message}")
                _promotions.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Aplica os filtros na lista em memória (fullList)
     */
    fun applyFilter(type: FilterType) {

        val currentList = fullList // Usa a lista cache para filtrar

        val filteredList = when (type) {
            FilterType.RECENT -> {
                currentList
            }

            FilterType.LOWEST_PRICE -> {
                // Ordena por preço (menor para maior)
                currentList.sortedBy { it.deal?.price?.amount ?: Double.MAX_VALUE }
            }

            FilterType.HIGHEST_DISCOUNT -> {
                // Ordena por desconto (maior para menor)
                currentList.sortedByDescending { it.deal?.cut ?: 0 }
            }

            FilterType.MOST_POPULAR -> {
                // Filtra e ordena pela popularidade (campo 'popularityRank')
                currentList
                    .filter { it.popularityRank != null }
                    .sortedBy { it.popularityRank }
            }
        }

        _promotions.value = filteredList
    }
}