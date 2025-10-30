package com.example.bossdrop.ui.login

import androidx.lifecycle.*
import com.example.bossdrop.data.repository.LoginRepository
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val repository = LoginRepository()

    private val _statusMessage = MutableLiveData<String>()
    val statusMessage: LiveData<String> = _statusMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _statusMessage.value = "Por favor, preencha todos os campos."
            return
        }

        viewModelScope.launch {
            try {
                _isLoading.value = true
                val success = repository.login(email, password)

                if (success) {
                    _statusMessage.value = "Login realizado com sucesso!"
                } else {
                    _statusMessage.value = "Email ou senha incorretos."
                }
            } catch (e: Exception) {
                _statusMessage.value = "Erro ao fazer login: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun goToRegister() {
        _statusMessage.value = "Abrindo a tela de cadastro..."
    }

    fun forgotPassword() {
        _statusMessage.value = "Abrindo a recuperação de senha..."
    }
}
