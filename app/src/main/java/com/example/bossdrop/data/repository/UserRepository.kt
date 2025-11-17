package com.example.bossdrop.data.repository

import android.util.Log
import com.example.bossdrop.data.model.User
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class EmailUpdateException(message: String) : Exception(message)
class PasswordUpdateException(message: String) : Exception(message)
class UsernameUpdateException(message: String) : Exception(message)

class UserRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    /**
     * Busca o objeto User completo (com nome, email e favoritos) do Firestore.
     */
    suspend fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser
        val uid = firebaseUser?.uid
        if (uid == null) {
            Log.w("UserRepository", "Nenhum usuário logado.")
            return null
        }

        return try {
            val document = usersCollection.document(uid).get().await()
            val user = document.toObject(User::class.java)

            if (user != null) {
                // Anexa os dados do Auth que não estão no Firestore
                user.email = firebaseUser.email ?: user.email

                // Verifica se o provedor é "google.com"
                val provider = firebaseUser.providerData.find {
                    it.providerId == "google.com"
                }
                user.providerId = provider?.providerId ?: "password" // Salva "google.com" or "password"
            }
            user
        } catch (e: Exception) {
            Log.e("UserRepository", "Erro ao buscar dados do usuário: ${e.message}")
            null
        }
    }

    /**
     * Verifica a senha atual do usuário antes de permitir mudanças sensíveis.
     */
    suspend fun reauthenticateUser(currentPassword: String): Boolean {
        val user = auth.currentUser
        val email = user?.email
        if (user == null || email == null) return false

        return try {
            val credential = EmailAuthProvider.getCredential(email, currentPassword)
            user.reauthenticate(credential).await()
            Log.d("UserRepository", "Re-autenticação bem-sucedida.")
            true
        } catch (e: Exception) {
            Log.w("UserRepository", "Falha na re-autenticação: ${e.message}")
            false
        }
    }

    suspend fun updateUsername(newUsername: String) {
        val uid = auth.currentUser?.uid ?: throw UsernameUpdateException("Usuário não encontrado")
        try {
            usersCollection.document(uid).update("username", newUsername).await()
        } catch (e: Exception) {
            Log.e("UserRepository", "UpdateUsername failed: ${e.message}")
            throw UsernameUpdateException(e.message ?: "Erro ao atualizar nome no Firestore")
        }
    }

    suspend fun updateEmail(newEmail: String) {
        val user = auth.currentUser ?: throw EmailUpdateException("Usuário não encontrado")
        try {
            // 1. Atualiza no Auth
            user.updateEmail(newEmail).await()
            // 2. Atualiza no Firestore
            usersCollection.document(user.uid).update("email", newEmail).await()
        } catch (e: Exception) {
            // Loga o erro real (ex: FirebaseAuthUserCollisionException)
            Log.e("UserRepository", "UpdateEmail failed: ${e.javaClass.simpleName}: ${e.message}")
            // Lança nossa exceção personalizada
            throw EmailUpdateException(e.message ?: "Erro do Firebase ao atualizar email")
        }
    }

    suspend fun updatePassword(newPassword: String) {
        val user = auth.currentUser ?: throw PasswordUpdateException("Usuário não encontrado")
        try {
            user.updatePassword(newPassword).await()
        } catch (e: Exception) {
            Log.e("UserRepository", "UpdatePassword failed: ${e.javaClass.simpleName}: ${e.message}")
            throw PasswordUpdateException(e.message ?: "Erro do Firebase ao atualizar senha")
        }
    }

    suspend fun sendPasswordResetEmail(email: String): Boolean {
        return try {
            auth.sendPasswordResetEmail(email).await()
            true
        } catch (e: Exception) { false }
    }
}