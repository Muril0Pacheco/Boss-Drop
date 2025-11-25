package com.example.bossdrop.data.repository

import android.util.Log
import com.example.bossdrop.data.model.FavoriteItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FavoriteRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {


    suspend fun addToFavorites(gameId: String, title: String, imageUrl: String?): Boolean {
        val uid = auth.currentUser?.uid ?: return false

        val favoriteData = hashMapOf(
            "gameId" to gameId,
            "gameTitle" to title,
            "gameImageUrl" to imageUrl
        )

        return try {
                db.collection("users").document(uid)
                .collection("wishlist")
                .document(gameId)
                .set(favoriteData)
                .await()

            val arrayUpdate = hashMapOf(
                "favoriteGameIds" to FieldValue.arrayUnion(gameId)
            )

            db.collection("users").document(uid)
                .set(arrayUpdate, com.google.firebase.firestore.SetOptions.merge())
                .await()

            true
        } catch (e: Exception) {
            Log.e("FavoriteRepository", "Erro ao salvar: ${e.message}")
            false
        }
    }


    suspend fun removeFromFavorites(gameId: String): Boolean {
        val uid = auth.currentUser?.uid ?: return false

        return try {
            db.collection("users").document(uid)
                .collection("wishlist")
                .document(gameId)
                .delete()

            db.collection("users").document(uid)
                .update("favoriteGameIds", FieldValue.arrayRemove(gameId))
                .await()

            Log.d("FavoriteRepository", "Jogo $gameId removido completamente.")
            true
        } catch (e: Exception) {
            Log.e("FavoriteRepository", "Erro ao remover favorito: ${e.message}")
            false
        }
    }

    private fun tryCreateUserDocAndAddFavorite(uid: String, gameId: String) {
        val data = hashMapOf("favoriteGameIds" to listOf(gameId))
        db.collection("users").document(uid).set(data, com.google.firebase.firestore.SetOptions.merge())
    }

    suspend fun getFavorites(): List<FavoriteItem> {
        val uid = auth.currentUser?.uid ?: return emptyList()
        return try {
            val snapshot = db.collection("users").document(uid)
                .collection("wishlist")
                .get()
                .await()

            snapshot.documents.map { doc ->
                FavoriteItem(
                    gameId = doc.getString("gameId") ?: "",
                    gameTitle = doc.getString("gameTitle") ?: "Sem Título",
                    gameImageUrl = doc.getString("gameImageUrl"),
                    gamePrice = "Ver Oferta",
                    gameDiscount = null
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}