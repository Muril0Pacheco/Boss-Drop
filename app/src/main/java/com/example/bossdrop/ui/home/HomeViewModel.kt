package com.example.bossdrop.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bossdrop.data.model.Deal // <-- IMPORT MUDOU
import com.example.bossdrop.data.repository.PromotionRepository
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val repository = PromotionRepository()

    // 1. O LiveData agora usa o nosso novo model "Deal"
    private val _promotions = MutableLiveData<List<Deal>>()
    val promotions: LiveData<List<Deal>> = _promotions

    // 2. Novo LiveData para controlar a exibição de um "loading"
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        // 3. Chama a função para carregar os dados assim que o ViewModel for criado
        loadPromotions()
    }

    /**
     * Busca as promoções da API.
     * Usa o viewModelScope para fazer a chamada em uma Coroutine.
     */
    fun loadPromotions() {
        // 4. Inicia a Coroutine
        viewModelScope.launch {
            try {
                // Avisa a UI que estamos carregando
                _isLoading.value = true

                // 5. Chama a função SUSPENSA do repositório
                val data = repository.getPromotionsFromApi()

                // 6. Posta os dados reais no LiveData
                _promotions.value = data

            } catch (e: Exception) {
                // Em caso de qualquer erro, loga e posta uma lista vazia
                Log.e("HomeViewModel", "Erro ao carregar promoções: ${e.message}")
                _promotions.value = emptyList()
            } finally {
                // 7. Esconde o "loading", independente de sucesso ou falha
                _isLoading.value = false
            }
        }
    }
}