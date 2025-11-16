package com.example.bossdrop.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// Enum para comunicar eventos de navegação para a Activity
enum class SettingsNavigation {
    TO_ACCOUNT,
    TO_NOTIFICATIONS,
    TO_PRIVACY,
    TO_HELP
}

class SettingsViewModel : ViewModel() {

    // 1. Estado (Dados)
    // O username seria buscado de um repositório (ex: UserRepository)
    private val _username = MutableLiveData<String>()
    val username: LiveData<String> = _username

    // 2. Eventos (Ações e Navegação)
    // Usamos MutableLiveData para eventos que só devem ser consumidos uma vez (como navegação)
    private val _navigationEvent = MutableLiveData<SettingsNavigation>()
    val navigationEvent: LiveData<SettingsNavigation> = _navigationEvent

    // Evento para o fluxo de "Sair da conta"
    private val _signOutEvent = MutableLiveData<Boolean>()
    val signOutEvent: LiveData<Boolean> = _signOutEvent

    init {
        // Simula o carregamento dos dados do usuário
        loadUserData()
    }

    private fun loadUserData() {
        // Na vida real, você chamaria:
        // viewModelScope.launch {
        //    val user = userRepository.getCurrentUser()
        //    _username.value = user.username
        // }
        _username.value = "@murilo_bossdrop" // Placeholder
    }

    // 3. Funções chamadas pela View (Cliques)
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
        // Inicia o fluxo de "Sair da conta"
        // A Activity vai observar isso e mostrar um diálogo de confirmação
        _signOutEvent.value = true
    }
}