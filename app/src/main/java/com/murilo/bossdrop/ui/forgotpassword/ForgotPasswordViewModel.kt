// ARQUIVO: ui/forgotpassword/ForgotPasswordViewModel.kt

package com.murilo.bossdrop.ui.forgotpassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.murilo.bossdrop.data.repository.UserRepository
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val repository: UserRepository = UserRepository()
) : ViewModel() {

    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")

    private val _statusMessage = MutableLiveData<String>()
    val statusMessage: LiveData<String> = _statusMessage

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun sendResetLink(email: String) {
        if (email.isEmpty() || !emailRegex.matches(email)) {
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