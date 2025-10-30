package com.example.bossdrop.ui.createnewpassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CreateNewPasswordViewModel : ViewModel() {

    private val _statusMessage = MutableLiveData<String>()
    val statusMessage: LiveData<String> = _statusMessage

    fun validatePasswords(newPassword: String, repeatPassword: String) {
        when {
            newPassword.isEmpty() || repeatPassword.isEmpty() -> {
                _statusMessage.value = "Preencha todos os campos."
            }
            newPassword != repeatPassword -> {
                _statusMessage.value = "As senhas não coincidem."
            }
            else -> {
                // Aqui você pode adicionar a lógica real de salvar no backend
                _statusMessage.value = "Senha alterada com sucesso!"
            }
        }
    }
}
