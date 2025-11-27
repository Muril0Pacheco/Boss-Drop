package com.murilo.bossdrop.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.murilo.bossdrop.data.repository.RegisterEmailInUseException
import com.murilo.bossdrop.data.repository.RegisterGenericException
import com.murilo.bossdrop.data.repository.RegisterRepository
import com.murilo.bossdrop.data.repository.RegisterWeakPasswordException
import kotlinx.coroutines.launch

enum class RegisterResultType {
    SUCCESS,
    ERROR_WEAK_PASSWORD,
    ERROR_EMAIL_IN_USE,
    ERROR_GENERIC
}

class RegisterViewModel(
    // Injeção de dependência
    private val repository: RegisterRepository = RegisterRepository()
) : ViewModel(){

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _registrationResult = MutableLiveData<Pair<RegisterResultType, String?>>()
    val registrationResult: LiveData<Pair<RegisterResultType, String?>> = _registrationResult

    fun register(username: String, email: String, password: String, confirmPassword: String) {

        if (password != confirmPassword) {
            // Erro de UI: Senhas não coincidem
            _registrationResult.value = Pair(RegisterResultType.ERROR_WEAK_PASSWORD, "As senhas não coincidem")
            return
        }
        if (password.length < 6) {
            // Erro de UI: Validação local (nem tenta ir ao Firebase)
            _registrationResult.value = Pair(RegisterResultType.ERROR_WEAK_PASSWORD, "A senha deve ter pelo menos 6 caracteres")
            return
        }

        _isLoading.value = true

        viewModelScope.launch {
            try {
                repository.registerUser(username, email, password)
                _registrationResult.value = Pair(RegisterResultType.SUCCESS, null)

            } catch (e: RegisterWeakPasswordException) {
                _registrationResult.value = Pair(RegisterResultType.ERROR_WEAK_PASSWORD, e.message)

            } catch (e: RegisterEmailInUseException) {
                _registrationResult.value = Pair(RegisterResultType.ERROR_EMAIL_IN_USE, null)

            } catch (e: RegisterGenericException) {
                _registrationResult.value = Pair(RegisterResultType.ERROR_GENERIC, e.message)

            } finally {
                _isLoading.value = false
            }
        }
    }
}