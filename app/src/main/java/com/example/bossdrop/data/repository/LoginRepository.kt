package com.example.bossdrop.data.repository

import kotlinx.coroutines.delay

class LoginRepository {

    // Simula uma chamada de autenticação
    suspend fun login(email: String, password: String): Boolean {
        delay(1000) // Simula tempo de rede (1s)

        // TODO: Substituir por lógica real de login (API ou Firebase)
        return (email == "teste@bossdrop.com" && password == "123456")
    }
}
