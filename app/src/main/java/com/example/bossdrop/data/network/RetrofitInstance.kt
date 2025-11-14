package com.example.bossdrop.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Objeto Singleton que cria e gerencia a instância do Retrofit
 * e expõe o serviço da API para o resto do app.
 */
object RetrofitInstance {

    // A URL base da API da CheapShark
    private const val BASE_URL = "https://www.cheapshark.com/api/1.0/"

    // Cria a instância do Retrofit de forma "lazy" (preguiçosa).
    // O código só será executado na primeira vez que "retrofit" for chamado.
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // O "tradutor" de JSON
            .build()
    }

    // Expõe publicamente a implementação da nossa interface ApiService.
    // Também é "lazy" e usa a instância do Retrofit acima.
    val api: CheapSharkApiService by lazy {
        retrofit.create(CheapSharkApiService::class.java)
    }
}