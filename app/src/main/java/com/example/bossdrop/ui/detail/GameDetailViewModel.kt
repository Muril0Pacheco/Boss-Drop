package com.example.bossdrop.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bossdrop.data.model.ItadPromotion
import com.example.bossdrop.data.repository.FavoriteRepository // Importar
import com.example.bossdrop.data.repository.PromotionRepository
import kotlinx.coroutines.launch

class GameDetailViewModel : ViewModel() {

    private val promotionRepository = PromotionRepository()
    private val favoriteRepository = FavoriteRepository() // Instanciar

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _dealDetails = MutableLiveData<ItadPromotion?>()
    val dealDetails: LiveData<ItadPromotion?> = _dealDetails

    // Novo: LiveData para o estado do botão de favorito
    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    private var currentGamaId: String? = null

    /**
     * Busca os detalhes completos de um único jogo no Firestore.
     */
    fun loadDetails(gameId: String) {
        currentGamaId = gameId // Salva o ID do jogo
        viewModelScope.launch {
            try {
                _isLoading.value = true

                // 1. Carrega os detalhes da promoção
                val promotion = promotionRepository.getPromotionById(gameId)
                _dealDetails.value = promotion

                // 2. Verifica se o jogo já é um favorito
                checkFavoriteStatus(gameId)

            } catch (e: Exception) {
                Log.e("GameDetailViewModel", "Erro ao carregar detalhes: ${e.message}")
                _dealDetails.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Novo: Verifica o estado de favorito
    private fun checkFavoriteStatus(gameId: String) {
        viewModelScope.launch {
            try {
                val favoriteIds = favoriteRepository.getFavoriteIds()
                _isFavorite.value = favoriteIds.contains(gameId)
            } catch (e: Exception) {
                Log.e("GameDetailViewModel", "Erro ao checar favoritos: ${e.message}")
                _isFavorite.value = false // Assume como não favorito em caso de erro
            }
        }
    }

    // Novo: Função para ser chamada pelo botão
    fun toggleFavorite() {
        val gameId = currentGamaId ?: return // Pega o ID salvo
        val currentlyFavorite = _isFavorite.value ?: false

        viewModelScope.launch {
            val success: Boolean
            if (currentlyFavorite) {
                // Remove dos favoritos
                success = favoriteRepository.removeFromFavorites(gameId)
                if (success) {
                    _isFavorite.value = false
                }
            } else {
                // Adiciona aos favoritos
                success = favoriteRepository.addToFavorites(gameId)
                if (success) {
                    _isFavorite.value = true
                }
            }

            if (!success) {
                Log.w("GameDetailViewModel", "Falha ao atualizar favorito no Firestore.")
                // (Opcional) Você pode postar um LiveData de erro para a Activity mostrar um Toast
            }
        }
    }
}