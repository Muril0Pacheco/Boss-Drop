package com.murilo.bossdrop.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.murilo.bossdrop.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class RegisterEmailInUseException(message: String) : RuntimeException(message)
class RegisterWeakPasswordException(message: String) : RuntimeException(message)
class RegisterGenericException(message: String) : RuntimeException(message)
class RegisterRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private val usersCollection = db.collection("users")

    /**
     * Tenta registrar o usuário.
     * Lança exceções específicas em caso de falha.
     */
    suspend fun registerUser(username: String, email: String, password: String) {
        try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user

            if (firebaseUser != null) {
                val newUser = User(
                    uid = firebaseUser.uid,
                    username = username,
                    email = email,
                    profileImageUrl = null,
                    favoriteGameIds = emptyList()
                )

                usersCollection.document(firebaseUser.uid).set(newUser).await()
                Log.d("RegisterRepo", "Usuário ${firebaseUser.uid} registrado.")
            } else {
                throw RegisterGenericException("Usuário não retornado após a autenticação.")
            }
        }
        catch (e: FirebaseAuthWeakPasswordException) {
            // Captura o erro de SENHA FRACA
            Log.w("RegisterRepo", "Senha fraca: ${e.message}")
            throw RegisterWeakPasswordException(e.reason ?: "A senha deve ter pelo menos 6 caracteres")
        }
        catch (e: FirebaseAuthUserCollisionException) {
            // Captura o erro de E-MAIL EM USO
            Log.w("RegisterRepo", "Email em uso: ${e.message}")
            throw RegisterEmailInUseException(e.message ?: "Este e-mail já está em uso")
        }
        catch (e: Exception) {
            // Captura todos os outros erros
            Log.e("RegisterRepo", "Falha genérica no registro: ${e.message}")
            throw RegisterGenericException(e.message ?: "Erro desconhecido")
        }
    }
}