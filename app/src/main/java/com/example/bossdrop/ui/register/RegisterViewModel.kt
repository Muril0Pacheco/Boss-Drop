package com.example.bossdrop.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bossdrop.data.repository.RegisterRepository
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val repository = RegisterRepository()

    // Controla o estado de Loading (mostra/esconde ProgressBar)
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // Sinaliza quando o cadastro foi concluído com sucesso
    private val _registrationSuccess = MutableLiveData<Boolean>()
    val registrationSuccess: LiveData<Boolean> = _registrationSuccess

    // Sinaliza se houve um erro (ex: "e-mail já em uso")
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    /**
     * Inicia o processo de cadastro chamando o Repositório.
     */
    fun register(username: String, email: String, password: String, confirmPassword: String) {
        // Validação básica do lado do ViewModel
        if (password != confirmPassword) {
            _errorMessage.value = "As senhas não coincidem."
            return
        }

        _isLoading.value = true
        _errorMessage.value = ""

        viewModelScope.launch {
            val success = repository.registerUser(username, email, password)

            if (success) {
                _registrationSuccess.value = true
            } else {
                // Aqui podemos adicionar lógica para mensagens de erro específicas do Firebase.
                // Por enquanto, enviamos uma mensagem genérica de falha no servidor.
                _errorMessage.value = "Falha ao realizar o cadastro. Tente um e-mail diferente."
            }

            _isLoading.value = false
        }
    }
}