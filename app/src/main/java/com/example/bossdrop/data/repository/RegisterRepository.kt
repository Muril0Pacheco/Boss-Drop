package com.example.bossdrop.data.repository

import android.util.Log
import com.example.bossdrop.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class RegisterRepository {

    // Instâncias do Firebase
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    /**
     * Tenta registrar o usuário no Firebase Authentication e salvar seus dados no Firestore.
     *
     * @return true se o registro e o salvamento de dados forem bem-sucedidos.
     */
    suspend fun registerUser(username: String, email: String, password: String): Boolean {
        // 1. Tenta criar o usuário no Firebase Auth
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user

            if (firebaseUser != null) {
                // 2. Cria o novo objeto User (sem foto de perfil)
                val newUser = User(
                    uid = firebaseUser.uid,
                    username = username,
                    email = email,
                    profileImageUrl = null, // Deixamos como null, conforme combinado
                    favoriteGameIds = emptyList()
                )

                // 3. Salva o documento User no Firestore
                usersCollection.document(firebaseUser.uid).set(newUser).await()

                Log.d("RegisterRepo", "Usuário ${firebaseUser.uid} registrado e dados salvos no Firestore.")
                true
            } else {
                Log.e("RegisterRepo", "Erro: Usuário não retornado após a autenticação.")
                false
            }
        } catch (e: Exception) {
            // Captura erros como: email já em uso, senha muito fraca, internet.
            Log.e("RegisterRepo", "Falha no registro: ${e.message}")
            false
        }
    }
}