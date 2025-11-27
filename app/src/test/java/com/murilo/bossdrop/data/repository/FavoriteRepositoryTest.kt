package com.murilo.bossdrop.data.repository

import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

class FavoriteRepositoryTest {

    @Mock
    lateinit var mockFirestore: FirebaseFirestore

    @Mock
    lateinit var mockAuth: FirebaseAuth

    @Mock
    lateinit var mockUser: FirebaseUser

    @Mock
    lateinit var mockCollection: CollectionReference

    @Mock
    lateinit var mockDocument: DocumentReference

    @Mock
    lateinit var mockQuerySnapshot: QuerySnapshot

    @Mock
    lateinit var mockDocumentSnapshot: DocumentSnapshot

    // Mock para o objeto FieldValue (arrayUnion/arrayRemove)
    @Mock
    lateinit var mockFieldValueObject: FieldValue

    // Mocks estáticos
    private lateinit var mockedLog: MockedStatic<Log>
    private lateinit var mockedFieldValue: MockedStatic<FieldValue>

    private lateinit var repository: FavoriteRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

        // Mockando Log e FieldValue (Classes estáticas)
        mockedLog = Mockito.mockStatic(Log::class.java)
        mockedFieldValue = Mockito.mockStatic(FieldValue::class.java)

        // Configurando comportamento padrão do FieldValue
        // Quando chamar FieldValue.arrayUnion(...), retorna nosso objeto mockado
        mockedFieldValue.`when`<FieldValue> { FieldValue.arrayUnion(any()) }.thenReturn(mockFieldValueObject)
        mockedFieldValue.`when`<FieldValue> { FieldValue.arrayRemove(any()) }.thenReturn(mockFieldValueObject)

        repository = FavoriteRepository(mockFirestore, mockAuth)
    }

    @After
    fun tearDown() {
        // Obrigatório fechar mocks estáticos
        mockedLog.close()
        mockedFieldValue.close()
    }

    @Test
    fun `addToFavorites deve retornar false se usuario nao logado`() = runBlocking {
        // User é null
        whenever(mockAuth.currentUser).thenReturn(null)

        val result = repository.addToFavorites("1", "Jogo", "img")

        assertFalse(result)
    }

    @Test
    fun `addToFavorites deve retornar true ao salvar com sucesso`() = runBlocking {
        // --- CENÁRIO ---
        val uid = "user123"
        whenever(mockAuth.currentUser).thenReturn(mockUser)
        whenever(mockUser.uid).thenReturn(uid)

        // Cadeia do Firestore
        whenever(mockFirestore.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document(anyString())).thenReturn(mockDocument)
        whenever(mockDocument.collection(anyString())).thenReturn(mockCollection) // Para subcollection 'wishlist'

        // Simula os .set() retornando Task de sucesso
        whenever(mockDocument.set(any())).thenReturn(Tasks.forResult(null))
        whenever(mockDocument.set(any(), any())).thenReturn(Tasks.forResult(null)) // Para o merge

        // --- AÇÃO ---
        val result = repository.addToFavorites("game1", "Zelda", "url")

        // --- VERIFICAÇÃO ---
        assertTrue(result)
    }

    @Test
    fun `removeFromFavorites deve retornar true ao remover com sucesso`() = runBlocking {
        // --- CENÁRIO ---
        val uid = "user123"
        whenever(mockAuth.currentUser).thenReturn(mockUser)
        whenever(mockUser.uid).thenReturn(uid)

        // Cadeia do Firestore
        whenever(mockFirestore.collection(anyString())).thenReturn(mockCollection)
        whenever(mockCollection.document(anyString())).thenReturn(mockDocument)
        whenever(mockDocument.collection(anyString())).thenReturn(mockCollection)

        // Simula delete() e update()
        whenever(mockDocument.delete()).thenReturn(Tasks.forResult(null))
        whenever(mockDocument.update(anyString(), any<FieldValue>())).thenReturn(Tasks.forResult(null))

        // --- AÇÃO ---
        val result = repository.removeFromFavorites("game1")

        // --- VERIFICAÇÃO ---
        assertTrue(result)
    }

    @Test
    fun `getFavorites deve retornar lista mapeada corretamente`() = runBlocking {
        // --- CENÁRIO ---
        val uid = "user123"
        whenever(mockAuth.currentUser).thenReturn(mockUser)
        whenever(mockUser.uid).thenReturn(uid)

        // Configurando retorno do banco
        whenever(mockFirestore.collection("users")).thenReturn(mockCollection)
        whenever(mockCollection.document(uid)).thenReturn(mockDocument)
        whenever(mockDocument.collection("wishlist")).thenReturn(mockCollection)
        whenever(mockCollection.get()).thenReturn(Tasks.forResult(mockQuerySnapshot))

        // Criando itens falsos dentro do Snapshot
        val doc1 = Mockito.mock(DocumentSnapshot::class.java)
        whenever(doc1.getString("gameId")).thenReturn("10")
        whenever(doc1.getString("gameTitle")).thenReturn("Mario")

        val doc2 = Mockito.mock(DocumentSnapshot::class.java)
        whenever(doc2.getString("gameId")).thenReturn("11")
        whenever(doc2.getString("gameTitle")).thenReturn("Sonic")

        whenever(mockQuerySnapshot.documents).thenReturn(listOf(doc1, doc2))

        // --- AÇÃO ---
        val list = repository.getFavorites()

        // --- VERIFICAÇÃO ---
        assertEquals(2, list.size)
        assertEquals("Mario", list[0].gameTitle)
        assertEquals("Sonic", list[1].gameTitle)
    }
}