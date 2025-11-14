package com.example.bossdrop.data.network

import com.example.bossdrop.data.model.Deal
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface que define todos os endpoints da API da CheapShark
 * que nosso app vai usar.
 */
interface CheapSharkApiService {

    /**
     * Busca a lista principal de promoções.
     * Corresponde a chamada: https://www.cheapshark.com/api/1.0/deals?...
     *
     * @param onSale Filtra para apenas promoções ativas (1 = true)
     * @param sortBy Por qual critério ordenar (ex: "DealRating", "Price")
     * @param pageSize O número máximo de resultados (API limita a 60)
     *
     * @return Uma lista de objetos Deal
     */
    @GET("deals")
    suspend fun getDeals(
        @Query("onSale") onSale: Int = 1,
        @Query("sortBy") sortBy: String = "DealRating",
        @Query("pageSize") pageSize: Int = 60
    ): List<Deal>

    // No futuro, poderíamos adicionar mais chamadas aqui, como:
    // @GET("games")
    // suspend fun searchGameByTitle(@Query("title") title: String): List<Game>
}