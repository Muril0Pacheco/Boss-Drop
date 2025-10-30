package com.example.bossdrop.data.repository

import kotlinx.coroutines.delay

class RegisterRepository {

    // Simula a chamada de um backend (API, Firebase, etc.)
    suspend fun registerUser(username: String, email: String, password: String): Boolean {
        delay(1500) // simula tempo de rede
        // TODO: Substituir por lógica real de API/Firebase
        return email != "jaexistente@bossdrop.com"
    }
}
