package com.example.bossdrop.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bossdrop.data.model.ItadPromotion
import com.example.bossdrop.data.repository.PromotionRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    // Injeção de dependência no construtor
    private val repository: PromotionRepository = PromotionRepository()
) : ViewModel(){

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

    var currentFilter: FilterType = FilterType.MOST_POPULAR
        private set

    init {
        loadPromotions(initialFilter = FilterType.MOST_POPULAR)
    }


    fun loadPromotions(initialFilter: FilterType = FilterType.RECENT) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val promotionList = repository.getPromotionsFromFirestore()

                fullList = promotionList

                applyFilter(initialFilter)

            } catch (e: Exception) {
                Log.e("HomeViewModel", "Erro ao carregar promoções: ${e.message}")
                _promotions.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun applyFilter(type: FilterType) {
        currentFilter = type

        val currentList = fullList

        val filteredList = when (type) {
            FilterType.RECENT -> {
                currentList
            }

            FilterType.LOWEST_PRICE -> {
                currentList.sortedBy { it.deal?.price?.amount ?: Double.MAX_VALUE }
            }

            FilterType.HIGHEST_DISCOUNT -> {
                currentList.sortedByDescending { it.deal?.cut ?: 0 }
            }

            FilterType.MOST_POPULAR -> {
                currentList
                    .filter { it.popularityRank != null }
                    .sortedBy { it.popularityRank }
            }
        }

        _promotions.value = filteredList
    }
}