package com.example.bossdrop.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bossdrop.data.model.ItadPromotion
import com.example.bossdrop.data.repository.PromotionRepository
import kotlinx.coroutines.launch

class GameDetailViewModel : ViewModel() {

    private val repository = PromotionRepository()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _dealDetails = MutableLiveData<ItadPromotion?>()
    val dealDetails: LiveData<ItadPromotion?> = _dealDetails

    /**
     * Busca os detalhes completos de um único jogo no Firestore.
     */
    fun loadDetailsFromFirestore(gameId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val promotion = repository.getPromotionById(gameId)
                _dealDetails.value = promotion
            } catch (e: Exception) {
                Log.e("GameDetailViewModel", "Erro ao carregar detalhes: ${e.message}")
                _dealDetails.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }
}