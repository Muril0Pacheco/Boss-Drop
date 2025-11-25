package com.example.bossdrop.data.repository

import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableReference
import com.google.firebase.functions.HttpsCallableResult
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class GameSearchRepositoryTest {

    // Mocks necessários para a cadeia do Firebase Functions
    @Mock
    lateinit var mockFunctions: FirebaseFunctions

    @Mock
    lateinit var mockCallable: HttpsCallableReference

    @Mock
    lateinit var mockResult: HttpsCallableResult

    // Mock do Log
    private lateinit var mockedLog: MockedStatic<Log>

    private lateinit var repository: GameSearchRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        mockedLog = Mockito.mockStatic(Log::class.java)

        // Injeta o Functions falso
        repository = GameSearchRepository(mockFunctions)
    }

    @After
    fun tearDown() {
        mockedLog.close()
    }

    @Test
    fun `searchGlobalGames deve retornar lista de jogos quando sucesso`() = runBlocking {
        // --- CENÁRIO ---
        val query = "Elden Ring"

        // Simula o JSON que viria da nuvem (Cloud Function)
        // O app espera uma List<Map<String, String>>
        val fakeCloudData = listOf(
            mapOf(
                "id" to "123",
                "title" to "Elden Ring",
                "slug" to "elden-ring",
                "boxart" to "http://imagem.com/elden.jpg"
            )
        )

        // Montando a cadeia de chamadas do Mockito:
        // 1. functions.getHttpsCallable("searchGames") -> retorna o mockCallable
        whenever(mockFunctions.getHttpsCallable("searchGames")).thenReturn(mockCallable)

        // 2. callable.call(data) -> retorna uma Task com o mockResult
        whenever(mockCallable.call(any())).thenReturn(Tasks.forResult(mockResult))

        // 3. result.data -> retorna nossa lista falsa
        whenever(mockResult.data).thenReturn(fakeCloudData)

        // --- AÇÃO ---
        val result = repository.searchGlobalGames(query)

        // --- VERIFICAÇÃO ---
        assertEquals(1, result.size)
        assertEquals("Elden Ring", result[0].title)
        assertEquals("http://imagem.com/elden.jpg", result[0].imageUrl)
    }

    @Test
    fun `searchGlobalGames deve retornar lista vazia quando erro`() = runBlocking {
        // --- CENÁRIO: Erro na chamada ---
        whenever(mockFunctions.getHttpsCallable("searchGames")).thenReturn(mockCallable)

        // Simula uma exceção ao tentar chamar a função
        whenever(mockCallable.call(any())).thenThrow(RuntimeException("Erro na Cloud Function"))

        // --- AÇÃO ---
        val result = repository.searchGlobalGames("Mario")

        // --- VERIFICAÇÃO ---
        assertTrue(result.isEmpty())
    }

    @Test
    fun `searchGlobalGames deve tratar retorno nulo ou invalido`() = runBlocking {
        // --- CENÁRIO: Função retorna algo que não é uma lista (ex: null) ---
        whenever(mockFunctions.getHttpsCallable("searchGames")).thenReturn(mockCallable)
        whenever(mockCallable.call(any())).thenReturn(Tasks.forResult(mockResult))

        // O data vem null
        whenever(mockResult.data).thenReturn(null)

        // --- AÇÃO ---
        val result = repository.searchGlobalGames("Zelda")

        // --- VERIFICAÇÃO ---
        assertTrue(result.isEmpty())
    }
}