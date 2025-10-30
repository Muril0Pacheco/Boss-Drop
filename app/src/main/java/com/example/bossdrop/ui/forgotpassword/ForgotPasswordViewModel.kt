package com.example.bossdrop.ui.forgotpassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ForgotPasswordViewModel : ViewModel() {

    private val _statusMessage = MutableLiveData<String>()
    val statusMessage: LiveData<String> = _statusMessage

    fun sendResetLink(email: String) {
        if (email.isEmpty()) {
            _statusMessage.value = "Por favor, insira seu email."
        } else {
            // Aqui você pode chamar Firebase Auth ou sua API
            _statusMessage.value = "Link enviado para $email"
        }
    }
}
