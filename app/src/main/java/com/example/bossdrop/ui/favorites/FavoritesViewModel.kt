package com.example.bossdrop.ui.favorites

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bossdrop.data.model.FavoriteItem
import com.example.bossdrop.data.model.ItadPromotion
import com.example.bossdrop.data.repository.FavoriteRepository
import com.example.bossdrop.data.repository.PromotionRepository
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class FavoritesViewModel : ViewModel() {

    private val favoriteRepository = FavoriteRepository()
    private val promotionRepository = PromotionRepository()

    private val _favorites = MutableLiveData<List<FavoriteItem>>()
    val favorites: LiveData<List<FavoriteItem>> = _favorites

    // Novo: LiveData para estado de carregamento
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                // 1. Pega a lista de IDs de jogos favoritos
                val favoriteIds = favoriteRepository.getFavoriteIds()

                if (favoriteIds.isEmpty()) {
                    _favorites.value = emptyList()
                    _isLoading.value = false
                    return@launch
                }

                // 2. Busca os detalhes de cada jogo favorito
                // (Nota: Isso faz N buscas. O ideal é ter uma função
                // no PromotionRepository que busque vários IDs de uma vez)

                val promotionList = mutableListOf<ItadPromotion>()
                for (id in favoriteIds) {
                    val promo = promotionRepository.getPromotionById(id)
                    if (promo != null) {
                        promotionList.add(promo)
                    }
                }

                // 3. Mapeia os dados para o FavoriteItem
                val favoriteItems = mapPromotionsToFavoriteItems(promotionList)
                _favorites.value = favoriteItems

            } catch (e: Exception) {
                Log.e("FavoritesViewModel", "Erro ao carregar favoritos: ${e.message}")
                _favorites.value = emptyList() // Retorna lista vazia em caso de erro
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Novo: Função de mapeamento
    private fun mapPromotionsToFavoriteItems(promotions: List<ItadPromotion>): List<FavoriteItem> {
        val brlFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

        return promotions.map { promo ->
            FavoriteItem(
                gameId = promo.id, // Assumindo que ItadPromotion tem um campo 'id'
                gameTitle = promo.title,
                gameImageUrl = promo.assets?.boxart ?: promo.assets?.banner600,
                gamePrice = brlFormat.format(promo.deal?.price?.amount ?: 0.0),
                gameDiscount = promo.deal?.cut?.let { "$it%" } // Ex: "90%"
            )
        }
    }
}