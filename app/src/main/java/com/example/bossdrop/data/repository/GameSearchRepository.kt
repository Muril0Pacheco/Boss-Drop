package com.example.bossdrop.data.repository

import android.util.Log
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.tasks.await

data class SearchResult(
    val id: String,
    val title: String,
    val slug: String,
    val imageUrl: String?
)

class GameSearchRepository(private val functions: FirebaseFunctions = FirebaseFunctions.getInstance()) {

    /**
     * Chama a Cloud Function 'searchGames' no backend para buscar jogos na API global.
     * @param query O nome do jogo pesquisado (ex: "Elden Ring")
     */
    suspend fun searchGlobalGames(query: String): List<SearchResult> {

        val data = hashMapOf(
            "query" to query
        )

        return try {
            Log.d("GameSearchRepository", "Chamando Cloud Function 'searchGames' para: $query")

            val result = functions
                .getHttpsCallable("searchGames")
                .call(data)
                .await()

            val listData = result.data as? List<Map<String, String>> ?: emptyList()

            val searchResults = listData.map { item ->
                SearchResult(
                    id = item["id"] ?: "",
                    title = item["title"] ?: "",
                    slug = item["slug"] ?: "",
                    imageUrl = item["boxart"]
                )
            }

            Log.d("GameSearchRepository", "Sucesso! Encontrados ${searchResults.size} jogos.")
            searchResults

        } catch (e: Exception) {
            Log.e("GameSearchRepository", "Erro ao buscar jogo na nuvem: ${e.message}")
            emptyList()
        }
    }
}