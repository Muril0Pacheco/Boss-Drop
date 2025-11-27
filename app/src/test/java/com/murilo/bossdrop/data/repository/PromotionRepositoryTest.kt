package com.murilo.bossdrop.data.repository

import android.util.Log
import com.murilo.bossdrop.data.model.ItadPromotion
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class PromotionRepositoryTest {

    // 1. Mocks definidos com anotação @Mock do Mockito
    @Mock
    lateinit var mockFirestore: FirebaseFirestore

    @Mock
    lateinit var mockCollection: CollectionReference

    @Mock
    lateinit var mockDocument: DocumentReference

    @Mock
    lateinit var mockSnapshot: QuerySnapshot

    @Mock
    lateinit var mockDocSnapshot: DocumentSnapshot

    // Para controlar a classe estática Log
    private lateinit var mockedLog: MockedStatic<Log>

    private lateinit var repository: PromotionRepository

    @Before
    fun setup() {
        // Inicializa os mocks anotados com @Mock
        MockitoAnnotations.openMocks(this)

        // Mocka a classe Log estática para não quebrar o teste (Log.d, Log.e)
        mockedLog = Mockito.mockStatic(Log::class.java)

        // Injeta o Firestore falso no Repositório
        repository = PromotionRepository(mockFirestore)
    }

    @After
    fun tearDown() {
        // É OBRIGATÓRIO fechar o mock estático depois de cada teste
        mockedLog.close()
    }

    @Test
    fun `getPromotionsFromFirestore deve retornar lista quando sucesso`() = runBlocking {
        // CENÁRIO
        val fakeList = listOf(ItadPromotion(title = "Jogo Teste Mockito"))

        // Configurando o comportamento do Mockito
        whenever(mockFirestore.collection("promocoes_br_v3")).thenReturn(mockCollection)

        // TRUQUE: Em vez de mockar o await(), retornamos uma Task já completada com sucesso
        whenever(mockCollection.get()).thenReturn(Tasks.forResult(mockSnapshot))

        // Quando pedir para converter os objetos, retorne nossa lista falsa
        whenever(mockSnapshot.toObjects(ItadPromotion::class.java)).thenReturn(fakeList)

        // AÇÃO
        val result = repository.getPromotionsFromFirestore()

        // VERIFICAÇÃO
        assertEquals(1, result.size)
        assertEquals("Jogo Teste Mockito", result[0].title)
    }

    @Test
    fun `getPromotionsFromFirestore deve retornar lista vazia quando erro`() = runBlocking {
        // CENÁRIO: Simulando erro ao chamar a collection
        whenever(mockFirestore.collection(anyString())).thenThrow(RuntimeException("Erro Mockito"))

        // AÇÃO
        val result = repository.getPromotionsFromFirestore()

        // VERIFICAÇÃO
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getPromotionById deve retornar promocao quando encontrado`() = runBlocking {
        // CENÁRIO
        val fakePromotion = ItadPromotion(title = "Jogo Individual")
        val gameId = "123"

        whenever(mockFirestore.collection("promocoes_br_v3")).thenReturn(mockCollection)
        whenever(mockCollection.document(gameId)).thenReturn(mockDocument)

        // Retorna Task completada com o snapshot do documento
        whenever(mockDocument.get()).thenReturn(Tasks.forResult(mockDocSnapshot))

        // Simula a conversão do documento para objeto
        whenever(mockDocSnapshot.toObject(ItadPromotion::class.java)).thenReturn(fakePromotion)

        // AÇÃO
        val result = repository.getPromotionById(gameId)

        // VERIFICAÇÃO
        assertEquals("Jogo Individual", result?.title)
    }
}