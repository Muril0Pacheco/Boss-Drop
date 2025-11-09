package com.example.bossdrop.ui.login

import androidx.lifecycle.*
import com.example.bossdrop.data.repository.LoginRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val repository = LoginRepository()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _statusMessage = MutableLiveData<String>()
    val statusMessage: LiveData<String> = _statusMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _navigateToHome = MutableLiveData<Boolean>()
    val navigateToHome: LiveData<Boolean> = _navigateToHome

    fun login(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        _isLoading.value = false
                        if (task.isSuccessful) {
                            _statusMessage.value = "Login realizado com sucesso!"
                            _navigateToHome.value = true
                        } else {
                            _statusMessage.value = "Falha no login: ${task.exception?.message}"
                        }
                    }
            } catch (e: Exception) {
                _statusMessage.value = "Erro: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun firebaseAuthWithGoogle(idToken: String) {
        _isLoading.value = true
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _statusMessage.value = "Login com Google realizado com sucesso!"
                        _navigateToHome.value = true
                    } else {
                        _statusMessage.value = "Falha na autenticação com Firebase: ${task.exception?.message}"
                    }
                    _isLoading.value = false
                }
        } catch (e: Exception) {
            _statusMessage.value = "Erro: ${e.message}"
            _isLoading.value = false
        }
    }

    fun onGoogleSignInFailed(message: String) {
        _statusMessage.value = message
    }

    fun onHomeNavigated() {
        _navigateToHome.value = false
    }

    fun goToRegister() {
        _statusMessage.value = "Abrindo a tela de cadastro..."
        // TODO: Mudar para um evento de navegação
    }

    fun forgotPassword() {
        _statusMessage.value = "Abrindo a recuperação de senha..."
        // TODO: Implementar lógica de recuperação de senha
    }
}