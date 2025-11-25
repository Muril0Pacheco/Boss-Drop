package com.example.bossdrop.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bossdrop.data.repository.UserRepository
import kotlinx.coroutines.launch

// Enum para comunicar eventos de navegação para a Activity
enum class SettingsNavigation {
    TO_ACCOUNT,
    TO_NOTIFICATIONS,
    TO_PRIVACY,
    TO_HELP
}

class SettingsViewModel(
    // Injeção: Recebe o repositório no construtor
    private val userRepository: UserRepository = UserRepository()
) : ViewModel(){

    // 1. Estado (Dados)
    private val _username = MutableLiveData<String>()
    val username: LiveData<String> = _username

    // 2. Eventos (Ações e Navegação)
    private val _navigationEvent = MutableLiveData<SettingsNavigation>()
    val navigationEvent: LiveData<SettingsNavigation> = _navigationEvent

    private val _signOutEvent = MutableLiveData<Boolean>()
    val signOutEvent: LiveData<Boolean> = _signOutEvent

    init {
        // Carrega os dados reais do usuário
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            val user = userRepository.getCurrentUser()
            // Se o usuário existir, use o username real.
            _username.value = user?.username ?: "@usuario_nao_encontrado"
        }
    }

    // 3. Funções chamadas pela View (Cliques) - Permanecem as mesmas
    fun onAccountClicked() {
        _navigationEvent.value = SettingsNavigation.TO_ACCOUNT
    }

    fun onNotificationsClicked() {
        _navigationEvent.value = SettingsNavigation.TO_NOTIFICATIONS
    }

    fun onPrivacyClicked() {
        _navigationEvent.value = SettingsNavigation.TO_PRIVACY
    }

    fun onHelpClicked() {
        _navigationEvent.value = SettingsNavigation.TO_HELP
    }

    fun onSignOutClicked() {
        _signOutEvent.value = true
    }
}