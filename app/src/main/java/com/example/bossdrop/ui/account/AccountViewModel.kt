package com.example.bossdrop.ui.account

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bossdrop.data.model.User
import com.example.bossdrop.data.repository.UserRepository
import kotlinx.coroutines.launch
import com.example.bossdrop.data.repository.EmailUpdateException
import com.example.bossdrop.data.repository.PasswordUpdateException

// Enum (continua igual)
enum class SaveResult {
    SUCCESS,
    ERROR_WRONG_PASSWORD,
    ERROR_INVALID_EMAIL,
    ERROR_EMAIL_IN_USE,
    ERROR_WEAK_PASSWORD,
    ERROR_GENERIC
}

class AccountViewModel(
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    private val _saveResult = MutableLiveData<Pair<SaveResult, String?>>()
    val saveResult: LiveData<Pair<SaveResult, String?>> = _saveResult

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _passwordResetEmailSent = MutableLiveData<Boolean>()
    val passwordResetEmailSent: LiveData<Boolean> = _passwordResetEmailSent

    init {
        loadCurrentUserDetails()
    }

    fun loadCurrentUserDetails() {
        _isLoading.value = true
        viewModelScope.launch {
            _currentUser.value = userRepository.getCurrentUser()
            _isLoading.value = false
        }
    }

    fun saveChanges(newUsername: String, newEmail: String, newPassword: String, currentPassword: String) {
        _isLoading.value = true

        val originalUser = _currentUser.value ?: return

        if (originalUser.providerId == "google.com") {

            if (newUsername.isNotBlank() && newUsername != originalUser.username) {
                viewModelScope.launch {
                    try {
                        userRepository.updateUsername(newUsername)
                        _saveResult.value = Pair(SaveResult.SUCCESS, null)
                    } catch (e: Exception) {
                        _saveResult.value = Pair(SaveResult.ERROR_GENERIC, e.message)
                    } finally {
                        _isLoading.value = false
                        loadCurrentUserDetails()
                    }
                }
            } else {
                _isLoading.value = false // Nada para salvar
            }

        } else {

            if (currentPassword.isBlank()) {
                _saveResult.value = Pair(SaveResult.ERROR_WRONG_PASSWORD, null)
                _isLoading.value = false
                return
            }
            if (newEmail.isNotBlank() && newEmail != originalUser.email &&
                !emailRegex.matches(newEmail)) {
                _saveResult.value = Pair(SaveResult.ERROR_INVALID_EMAIL, null)
                _isLoading.value = false
                return
            }

            viewModelScope.launch {
                val reauthenticated = userRepository.reauthenticateUser(currentPassword)
                if (!reauthenticated) {
                    _saveResult.value = Pair(SaveResult.ERROR_WRONG_PASSWORD, null)
                    _isLoading.value = false
                    return@launch
                }

                try {
                    if (newUsername.isNotBlank() && newUsername != originalUser.username) {
                        userRepository.updateUsername(newUsername)
                    }
                    if (newEmail.isNotBlank() && newEmail != originalUser.email) {
                        userRepository.updateEmail(newEmail)
                    }
                    if (newPassword.isNotBlank()) {
                        userRepository.updatePassword(newPassword)
                    }
                    _saveResult.value = Pair(SaveResult.SUCCESS, null)

                } catch (e: Exception) {
                    Log.w("AccountViewModel", "Falha ao salvar: ${e.javaClass.simpleName} - ${e.message}")
                    val errorMsg = e.message ?: "Erro desconhecido"
                    when (e) {
                        is EmailUpdateException -> {
                            if (errorMsg.contains("EMAIL_ALREADY_IN_USE", ignoreCase = true)) {
                                _saveResult.value = Pair(SaveResult.ERROR_EMAIL_IN_USE, null)
                            } else if (errorMsg.contains("RECENT_LOGIN_REQUIRED", ignoreCase = true)) {
                                _saveResult.value = Pair(SaveResult.ERROR_WRONG_PASSWORD, null)
                            } else {
                                _saveResult.value = Pair(SaveResult.ERROR_GENERIC, errorMsg)
                            }
                        }
                        is PasswordUpdateException -> {
                            if (errorMsg.contains("WEAK_PASSWORD", ignoreCase = true)) {
                                _saveResult.value = Pair(SaveResult.ERROR_WEAK_PASSWORD, null)
                            } else {
                                _saveResult.value = Pair(SaveResult.ERROR_GENERIC, errorMsg)
                            }
                        }
                        else -> {
                            _saveResult.value = Pair(SaveResult.ERROR_GENERIC, errorMsg)
                        }
                    }
                } finally {
                    _isLoading.value = false
                    loadCurrentUserDetails()
                }
            }
        }
    }

    fun onForgotPasswordClicked() {
        val email = _currentUser.value?.email
        if (email.isNullOrBlank()) return

        viewModelScope.launch {
            val sent = userRepository.sendPasswordResetEmail(email)
            _passwordResetEmailSent.value = sent
        }
    }
}