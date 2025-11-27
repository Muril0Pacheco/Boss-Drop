package com.murilo.bossdrop.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.murilo.bossdrop.data.model.ItadPromotion
import com.murilo.bossdrop.data.repository.FavoriteRepository
import com.murilo.bossdrop.data.repository.PromotionRepository
import kotlinx.coroutines.launch

class GameDetailViewModel(
    private val promotionRepository: PromotionRepository = PromotionRepository(),
    private val favoriteRepository: FavoriteRepository = FavoriteRepository()
) : ViewModel() {


    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _dealDetails = MutableLiveData<ItadPromotion?>()
    val dealDetails: LiveData<ItadPromotion?> = _dealDetails

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    private var currentGamaId: String? = null

    private var fallbackTitle: String = ""
    private var fallbackImage: String? = null

    fun setInitialData(gameId: String, title: String?, image: String?) {
        this.currentGamaId = gameId
        if (title != null) this.fallbackTitle = title
        this.fallbackImage = image
    }

    fun loadDetails(gameId: String) {
        currentGamaId = gameId

        viewModelScope.launch {
            try {
                _isLoading.value = true

                val promotion = promotionRepository.getPromotionById(gameId)
                _dealDetails.value = promotion

                checkFavoriteStatus(gameId)

            } catch (e: Exception) {
                Log.e("GameDetailViewModel", "Erro: ${e.message}")
                _dealDetails.value = null
                // Mesmo se der erro na API, verificamos se é favorito para o botão funcionar
                checkFavoriteStatus(gameId)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun checkFavoriteStatus(gameId: String) {
        viewModelScope.launch {
            try {
                val favoriteItems = favoriteRepository.getFavorites()
                val favoriteIds = favoriteItems.map { it.gameId }
                _isFavorite.value = favoriteIds.contains(gameId)
            } catch (e: Exception) {
                _isFavorite.value = false
            }
        }
    }

    fun toggleFavorite() {
        val gameId = currentGamaId ?: return
        val currentlyFavorite = _isFavorite.value ?: false

        viewModelScope.launch {
            val success: Boolean

            if (currentlyFavorite) {

                success = favoriteRepository.removeFromFavorites(gameId)
                if (success) {
                    _isFavorite.value = false
                }
            } else {

                val titleToAdd = _dealDetails.value?.title ?: fallbackTitle
                val imageToAdd = _dealDetails.value?.assets?.boxart ?: fallbackImage

                // Só adiciona se tivermos pelo menos um título
                if (titleToAdd.isNotEmpty()) {
                    success = favoriteRepository.addToFavorites(
                        gameId = gameId,
                        title = titleToAdd,
                        imageUrl = imageToAdd
                    )
                    if (success) {
                        _isFavorite.value = true
                    }
                } else {
                    success = false
                    Log.w("GameDetailViewModel", "Dados insuficientes para favoritar.")
                }
            }
        }
    }
}