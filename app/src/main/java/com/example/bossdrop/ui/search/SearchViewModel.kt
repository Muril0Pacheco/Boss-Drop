package com.example.bossdrop.ui.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bossdrop.data.model.ItadPromotion
import com.example.bossdrop.data.repository.PromotionRepository
import kotlinx.coroutines.launch

class SearchViewModel(
    // Injeção de dependência
    private val repository: PromotionRepository = PromotionRepository()
) : ViewModel() {

    private var _allDealsCache: List<ItadPromotion> = emptyList()

    private val _searchHistory = MutableLiveData<List<String>>()
    val searchHistory: LiveData<List<String>> = _searchHistory

    private val _dealList = MutableLiveData<List<ItadPromotion>>()
    val dealList: LiveData<List<ItadPromotion>> = _dealList

    private val _isSearchMode = MutableLiveData<Boolean>(false)
    val isSearchMode: LiveData<Boolean> = _isSearchMode

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val allDeals = repository.getPromotionsFromFirestore()

                _allDealsCache = allDeals

                val recommendedList = _allDealsCache
                    .filter { it.popularityRank != null }
                    .sortedBy { it.popularityRank!! }
                    .take(10)

                _dealList.value = recommendedList

            } catch (e: Exception) {
                Log.e("SearchViewModel", "Erro ao carregar recomendados: ${e.message}")
                _dealList.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchForGame(query: String) {
        if (query.isBlank()) {
            _isSearchMode.value = false
            _dealList.value = _allDealsCache
                .filter { it.popularityRank != null }
                .sortedBy { it.popularityRank!! }
                .take(10)
            return
        }

        _isSearchMode.value = true

        val searchResults = _allDealsCache.filter {
            it.title.contains(query, ignoreCase = true)
        }

        _dealList.value = searchResults
    }
}