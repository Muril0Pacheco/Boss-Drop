package com.example.bossdrop.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bossdrop.data.model.FavoriteItem
import com.example.bossdrop.data.repository.FavoriteRepository
import kotlinx.coroutines.launch

class FavoritesViewModel : ViewModel() {

    private val favoriteRepository = FavoriteRepository()

    private val _favorites = MutableLiveData<List<FavoriteItem>>()
    val favorites: LiveData<List<FavoriteItem>> = _favorites

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            _isLoading.value = true

            val items = favoriteRepository.getFavorites()

            _favorites.value = items
            _isLoading.value = false
        }
    }
}