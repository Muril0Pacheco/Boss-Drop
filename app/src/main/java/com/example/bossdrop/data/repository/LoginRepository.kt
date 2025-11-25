package com.example.bossdrop.data.repository

import android.util.Log
import com.example.bossdrop.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class LoginRepository (
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
){
    private val usersCollection = db.collection("users")

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

    fun firebaseAuthWithGoogle(idToken: String, onComplete: (Boolean, String?) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
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

    private fun checkAndCreateUserDocument(firebaseUser: FirebaseUser, onComplete: (Boolean, String?) -> Unit) {
        val docRef = usersCollection.document(firebaseUser.uid)
        docRef.get().addOnCompleteListener { docTask ->
            if (docTask.isSuccessful) {
                val document = docTask.result
                if (document != null && document.exists()) {
                    Log.d("LoginRepository", "Usuário Google já existe no Firestore.")
                    onComplete(true, null)
                } else {
                    Log.d("LoginRepository", "Usuário Google novo. Criando documento no Firestore...")

                    val username = firebaseUser.email?.split("@")?.get(0) ?: "usuario_google"

                    val newUser = User(
                        uid = firebaseUser.uid,
                        username = username,
                        email = firebaseUser.email ?: "",
                        profileImageUrl = null,
                        favoriteGameIds = emptyList()
                    )

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

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}