package com.example.bossdrop.ui.favorites

import com.example.bossdrop.R
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bossdrop.data.model.FavoriteItem

class FavoritesViewModel : ViewModel() {

    private val _favorites = MutableLiveData<List<FavoriteItem>>()
    val favorites: LiveData<List<FavoriteItem>> = _favorites

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        // Aqui você pode futuramente buscar da API ou banco de dados
        _favorites.value = listOf(

            FavoriteItem("GTA V", R.drawable.gta_cover, "R$ 99,90")
        )
    }
}
