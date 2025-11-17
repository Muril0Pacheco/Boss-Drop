package com.example.bossdrop.data.repository

import android.util.Log
import com.example.bossdrop.data.model.User
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class LoginRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    // 1. Login com E-mail/Senha (Sem alteração)
    fun login(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception?.message ?: "Erro desconhecido.")
                }
            }
    }

    // 2. Login com Google (Lógica de verificação adicionada)
    fun firebaseAuthWithGoogle(idToken: String, onComplete: (Boolean, String?) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Login no Auth foi sucesso, agora verifica/cria o documento no Firestore
                    val firebaseUser = task.result?.user
                    if (firebaseUser != null) {
                        checkAndCreateUserDocument(firebaseUser, onComplete)
                    } else {
                        onComplete(false, "Falha ao obter dados do usuário do Google.")
                    }
                } else {
                    onComplete(false, task.exception?.message ?: "Falha na autenticação com Firebase.")
                }
            }
    }

    // 3. Verifica se o usuário existe no Firestore; se não, cria.
    private fun checkAndCreateUserDocument(firebaseUser: FirebaseUser, onComplete: (Boolean, String?) -> Unit) {
        val docRef = usersCollection.document(firebaseUser.uid)
        docRef.get().addOnCompleteListener { docTask ->
            if (docTask.isSuccessful) {
                val document = docTask.result
                if (document != null && document.exists()) {
                    // 1. Documento já existe. Login concluído.
                    Log.d("LoginRepository", "Usuário Google já existe no Firestore.")
                    onComplete(true, null)
                } else {
                    // 2. Documento NÃO existe. Precisamos criá-lo.
                    Log.d("LoginRepository", "Usuário Google novo. Criando documento no Firestore...")

                    // Tenta extrair um username do email (ex: "murilo.teste" de "murilo.teste@gmail.com")
                    val username = firebaseUser.email?.split("@")?.get(0) ?: "usuario_google"

                    val newUser = User(
                        uid = firebaseUser.uid,
                        username = username,
                        email = firebaseUser.email ?: "",
                        profileImageUrl = null, // Sem foto de perfil
                        favoriteGameIds = emptyList() // Lista de favoritos vazia
                    )

                    // Salva o novo usuário no Firestore
                    docRef.set(newUser).addOnCompleteListener { setTask ->
                        if (setTask.isSuccessful) {
                            onComplete(true, null)
                        } else {
                            onComplete(false, "Falha ao salvar dados do usuário no Firestore.")
                        }
                    }
                }
            } else {
                onComplete(false, "Falha ao verificar usuário no Firestore.")
            }
        }
    }

    // Verifica se o usuário já está logado (Sem alteração)
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}