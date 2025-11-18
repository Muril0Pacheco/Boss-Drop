package com.example.bossdrop.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FavoriteRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val usersCollection = db.collection("users")

    /**
     * Adiciona um ID de jogo à lista de favoritos do usuário logado.
     */
    suspend fun addToFavorites(gameId: String): Boolean {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            Log.w("FavoriteRepository", "Usuário não logado. Falha ao adicionar favorito.")
            return false
        }

        return try {
            usersCollection.document(uid).update("favoriteGameIds", FieldValue.arrayUnion(gameId)).await()
            Log.d("FavoriteRepository", "Jogo $gameId adicionado aos favoritos.")
            true
        } catch (e: Exception) {
            Log.e("FavoriteRepository", "Erro ao adicionar favorito: ${e.message}")
            false
        }
    }

    /**
     * Remove um ID de jogo da lista de favoritos do usuário logado.
     */
    suspend fun removeFromFavorites(gameId: String): Boolean {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            Log.w("FavoriteRepository", "Usuário não logado. Falha ao remover favorito.")
            return false
        }

        return try {
            usersCollection.document(uid).update("favoriteGameIds", FieldValue.arrayRemove(gameId)).await()
            Log.d("FavoriteRepository", "Jogo $gameId removido dos favoritos.")
            true
        } catch (e: Exception) {
            Log.e("FavoriteRepository", "Erro ao remover favorito: ${e.message}")
            false
        }
    }

    /**
     * Busca a lista de IDs de jogos favoritos do usuário logado.
     */
    suspend fun getFavoriteIds(): List<String> {
        val uid = auth.currentUser?.uid
        if (uid == null) return emptyList()

        return try {
            val document = usersCollection.document(uid).get().await()
            // Assumimos que o campo é salvo como List<String>
            (document.get("favoriteGameIds") as? List<String>) ?: emptyList()
        } catch (e: Exception) {
            Log.e("FavoriteRepository", "Erro ao buscar lista de favoritos: ${e.message}")
            emptyList()
        }
    }
}