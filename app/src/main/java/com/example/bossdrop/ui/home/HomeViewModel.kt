package com.example.bossdrop.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bossdrop.data.model.ItadPromotion // ◀️ -- IMPORT CORRIGIDO
import com.example.bossdrop.data.repository.PromotionRepository
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val repository = PromotionRepository()

    // 1. O LiveData agora usa o nosso novo model "ItadPromotion"
    private val _promotions = MutableLiveData<List<ItadPromotion>>()
    val promotions: LiveData<List<ItadPromotion>> = _promotions

    // 2. Novo LiveData para controlar a exibição de um "loading"
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        // 3. Chama a função para carregar os dados assim que o ViewModel for criado
        loadPromotions()
    }

    /**
     * Busca as promoções do Firestore.
     * Usa o viewModelScope para fazer a chamada em uma Coroutine.
     */
    fun loadPromotions() {
        // 4. Inicia a Coroutine
        viewModelScope.launch {
            try {
                // Avisa a UI que estamos carregando
                _isLoading.value = true

                // 5. Chama a função CORRETA do repositório (getPromotionsFromFirestore)
                val data = repository.getPromotionsFromFirestore()

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