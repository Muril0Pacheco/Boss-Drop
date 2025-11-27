package com.murilo.bossdrop.data.repository

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

class LoginRepositoryTest {

    @Mock
    lateinit var mockAuth: FirebaseAuth

    @Mock
    lateinit var mockFirestore: FirebaseFirestore

    // ADICIONADO: Precisamos desse mock para a inicialização da classe não quebrar
    @Mock
    lateinit var mockCollection: CollectionReference

    @Mock
    lateinit var mockAuthTask: Task<AuthResult>

    @Mock
    lateinit var mockFirebaseUser: FirebaseUser

    @Captor
    lateinit var callbackCaptor: ArgumentCaptor<OnCompleteListener<AuthResult>>

    private lateinit var repository: LoginRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

        // CORREÇÃO: Ensinamos o Firestore a retornar a coleção ANTES de criar o Repositório
        whenever(mockFirestore.collection("users")).thenReturn(mockCollection)

        repository = LoginRepository(mockAuth, mockFirestore)
    }

    @Test
    fun `login deve retornar sucesso quando Firebase autenticar`() {
        // --- CENÁRIO ---
        val email = "teste@email.com"
        val senha = "123"
        var resultadoSucesso: Boolean? = null
        var resultadoErro: String? = null

        // Simula que o signIn retorna nossa Task falsa
        whenever(mockAuth.signInWithEmailAndPassword(email, senha)).thenReturn(mockAuthTask)

        // Simula que a Task foi bem sucedida
        whenever(mockAuthTask.isSuccessful).thenReturn(true)

        // --- AÇÃO ---
        repository.login(email, senha) { sucesso, erro ->
            resultadoSucesso = sucesso
            resultadoErro = erro
        }

        // --- DISPARAR CALLBACK MANUALMENTE ---
        // Capturamos o listener que o repositório passou para o Firebase
        verify(mockAuthTask).addOnCompleteListener(callbackCaptor.capture())

        // Executamos o onComplete simulando a resposta do Firebase
        callbackCaptor.value.onComplete(mockAuthTask)

        // --- VERIFICAÇÃO ---
        assertTrue(resultadoSucesso == true)
        assertEquals(null, resultadoErro)
    }

    @Test
    fun `login deve retornar erro quando Firebase falhar`() {
        // --- CENÁRIO ---
        var resultadoSucesso: Boolean? = null
        var resultadoErro: String? = null

        whenever(mockAuth.signInWithEmailAndPassword(any(), any())).thenReturn(mockAuthTask)

        // Configura a task para FALHA
        whenever(mockAuthTask.isSuccessful).thenReturn(false)
        whenever(mockAuthTask.exception).thenReturn(Exception("Senha incorreta"))

        // --- AÇÃO ---
        repository.login("email", "senha") { sucesso, erro ->
            resultadoSucesso = sucesso
            resultadoErro = erro
        }

        // --- DISPARAR CALLBACK MANUALMENTE ---
        verify(mockAuthTask).addOnCompleteListener(callbackCaptor.capture())
        callbackCaptor.value.onComplete(mockAuthTask)

        // --- VERIFICAÇÃO ---
        assertTrue(resultadoSucesso == false)
        assertEquals("Senha incorreta", resultadoErro)
    }

    @Test
    fun `getCurrentUser deve retornar usuario se logado`() {
        // Simula que existe um usuário logado
        whenever(mockAuth.currentUser).thenReturn(mockFirebaseUser)

        val result = repository.getCurrentUser()

        assertEquals(mockFirebaseUser, result)
    }

    @Test
    fun `getCurrentUser deve retornar null se deslogado`() {
        // Simula que NÃO existe usuário
        whenever(mockAuth.currentUser).thenReturn(null)

        val result = repository.getCurrentUser()

        assertEquals(null, result)
    }
}