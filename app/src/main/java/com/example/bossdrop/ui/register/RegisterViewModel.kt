package com.example.bossdrop.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bossdrop.data.repository.RegisterRepository
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val repository = RegisterRepository()

    private val _statusMessage = MutableLiveData<String>()
    val statusMessage: LiveData<String> = _statusMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun register(username: String, email: String, password: String, repeatPassword: String) {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty()) {
            _statusMessage.value = "Preencha todos os campos."
            return
        }

        if (password != repeatPassword) {
            _statusMessage.value = "As senhas não coincidem."
            return
        }

        viewModelScope.launch {
            try {
                _isLoading.value = true
                val success = repository.registerUser(username, email, password)

                if (success) {
                    _statusMessage.value = "Usuário cadastrado com sucesso!"
                } else {
                    _statusMessage.value = "Email já cadastrado."
                }
            } catch (e: Exception) {
                _statusMessage.value = "Erro ao cadastrar: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun chooseProfilePhoto() {
        _statusMessage.value = "Escolher foto de perfil"
    }
}
