package com.example.bossdrop.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bossdrop.data.model.Promotion
import com.example.bossdrop.data.repository.PromotionRepository

class HomeViewModel : ViewModel() {

    private val repository = PromotionRepository()

    private val _promotions = MutableLiveData<List<Promotion>>()
    val promotions: LiveData<List<Promotion>> = _promotions

    fun loadPromotions() {
        // No futuro, pode chamar API ou banco
        val data = repository.getPromotions()
        _promotions.value = data
    }
}
