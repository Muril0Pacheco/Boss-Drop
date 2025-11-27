package com.murilo.bossdrop.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.murilo.bossdrop.data.model.FavoriteItem
import com.murilo.bossdrop.data.repository.FavoriteRepository
import kotlinx.coroutines.launch

class FavoritesViewModel(
    // Injeção: Adicione o repositório no construtor
    private val favoriteRepository: FavoriteRepository = FavoriteRepository()
) : ViewModel() {

    private val _favorites = MutableLiveData<List<FavoriteItem>>()
    val favorites: LiveData<List<FavoriteItem>> = _favorites

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            try {
                    _isLoading.value = true
                    val items = favoriteRepository.getFavorites()
                    _favorites.value = items
                } catch (e: Exception) {
                    _favorites.value = emptyList()
                } finally {
                    _isLoading.value = false
            }
            }
        }
    }