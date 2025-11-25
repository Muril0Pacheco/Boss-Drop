package com.example.bossdrop.ui.login

import androidx.lifecycle.*
import com.example.bossdrop.data.repository.LoginRepository
class LoginViewModel(
    // Injeção: Adicione o repositório no construtor
    private val repository: LoginRepository = LoginRepository()
) : ViewModel(){

    private val _statusMessage = MutableLiveData<String>()
    val statusMessage: LiveData<String> = _statusMessage

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _navigateToHome = MutableLiveData<Boolean>()
    val navigateToHome: LiveData<Boolean> = _navigateToHome

    init {
        // Verifica se o usuário já está logado ao iniciar o ViewModel
        if (repository.getCurrentUser() != null) {
            _navigateToHome.value = true
        }
    }

    // 1. Login com E-mail/Senha (Chama o Repositório)
    fun login(email: String, password: String) {
        _isLoading.value = true
        // Não precisamos de viewModelScope.launch pois o Repositório usa Listeners
        repository.login(email, password) { success, errorMessage ->
            _isLoading.value = false
            if (success) {
                _statusMessage.value = "Login realizado com sucesso!"
                _navigateToHome.value = true
            } else {
                _statusMessage.value = "Falha no login: $errorMessage"
            }
        }
    }

    // 2. Login com Google (Chama o Repositório, Mantendo a Lógica do IdToken)
    fun firebaseAuthWithGoogle(idToken: String) {
        _isLoading.value = true
        repository.firebaseAuthWithGoogle(idToken) { success, errorMessage ->
            _isLoading.value = false
            if (success) {
                _statusMessage.value = "Login com Google realizado com sucesso!"
                _navigateToHome.value = true
            } else {
                _statusMessage.value = "Falha na autenticação com Firebase: $errorMessage"
            }
        }
    }

    fun onGoogleSignInFailed(message: String) {
        _statusMessage.value = message
        _isLoading.value = false
    }

    fun onHomeNavigated() {
        _navigateToHome.value = false
    }

    fun goToRegister() {
        _statusMessage.value = "Abrindo a tela de cadastro..."
    }

    fun forgotPassword() {
        _statusMessage.value = "Abrindo a recuperação de senha..."
    }
}