package com.example.bossdrop.ui.forgotpassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bossdrop.data.repository.UserRepository
import kotlinx.coroutines.launch

class ForgotPasswordViewModel : ViewModel() {

    private val repository = UserRepository()
    private val _statusMessage = MutableLiveData<String>()
    val statusMessage: LiveData<String> = _statusMessage

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun sendResetLink(email: String) {
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _statusMessage.value = "Por favor, insira um email válido."
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            val success = repository.sendPasswordResetEmail(email)

            if (success) {
                _statusMessage.value = "Link enviado para $email"
            } else {
                _statusMessage.value = "Erro ao enviar o link. Tente novamente."
            }
            _isLoading.value = false
        }
    }
}