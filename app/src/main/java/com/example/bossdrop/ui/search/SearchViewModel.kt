package com.example.bossdrop.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchViewModel : ViewModel() {

    // Lista "Mais Buscados"
    private val _searchHistory = MutableLiveData<List<String>>()
    val searchHistory: LiveData<List<String>> = _searchHistory

    // Número de itens recomendados (pode ser trocado depois por dados reais)
    private val _recommendedCount = MutableLiveData<Int>()
    val recommendedCount: LiveData<Int> = _recommendedCount

    init {
        loadSampleData()
    }

    // Simula carregamento de dados iniciais
    private fun loadSampleData() {
        _searchHistory.value = listOf("Hollow Knight", "EA 26", "Battlefield 6")
        _recommendedCount.value = 6
    }
}
