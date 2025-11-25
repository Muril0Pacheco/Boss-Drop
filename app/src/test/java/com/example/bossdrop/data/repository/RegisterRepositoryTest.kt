package com.example.bossdrop.data.repository

import android.util.Log
import com.example.bossdrop.data.model.User
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

class RegisterRepositoryTest {

    @Mock
    lateinit var mockAuth: FirebaseAuth

    @Mock
    lateinit var mockFirestore: FirebaseFirestore

    @Mock
    lateinit var mockCollection: CollectionReference

    @Mock
    lateinit var mockDocument: DocumentReference

    @Mock
    lateinit var mockAuthResult: AuthResult

    @Mock
    lateinit var mockFirebaseUser: FirebaseUser

    // Mock do Log estático
    private lateinit var mockedLog: MockedStatic<Log>

    private lateinit var repository: RegisterRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

        // Mock do Log para evitar erros
        mockedLog = Mockito.mockStatic(Log::class.java)

        // 1. PREVENÇÃO DE NULLPOINTER:
        // Ensinamos o Firestore a devolver a collection ANTES de criar o repository
        whenever(mockFirestore.collection("users")).thenReturn(mockCollection)

        // Inicializamos o repository com os mocks
        repository = RegisterRepository(mockAuth, mockFirestore)
    }

    @After
    fun tearDown() {
        mockedLog.close()
    }

    @Test
    fun `registerUser deve salvar usuario no firestore quando sucesso`() = runBlocking {
        // --- CENÁRIO ---
        val email = "novo@email.com"
        val senha = "123"
        val username = "novouser"
        val uid = "uid123"

        // Configurar Auth para sucesso
        whenever(mockAuth.createUserWithEmailAndPassword(email, senha))
            .thenReturn(Tasks.forResult(mockAuthResult)) // Retorna Task completada

        whenever(mockAuthResult.user).thenReturn(mockFirebaseUser)
        whenever(mockFirebaseUser.uid).thenReturn(uid)
        whenever(mockFirebaseUser.email).thenReturn(email)

        // Configurar Firestore para sucesso
        whenever(mockCollection.document(uid)).thenReturn(mockDocument)
        whenever(mockDocument.set(any<User>())).thenReturn(Tasks.forResult(null))

        // --- AÇÃO ---
        try {
            repository.registerUser(username, email, senha)
            // Se chegou aqui sem erro, passou
        } catch (e: Exception) {
            fail("Não deveria ter lançado exceção: ${e.message}")
        }
    }

    @Test(expected = RegisterWeakPasswordException::class)
    fun `registerUser deve lancar RegisterWeakPasswordException quando senha fraca`() = runBlocking {
        // --- CENÁRIO ---
        val exceptionFirebase = Mockito.mock(FirebaseAuthWeakPasswordException::class.java)
        whenever(exceptionFirebase.reason).thenReturn("Senha muito curta")
        whenever(exceptionFirebase.message).thenReturn("Erro senha fraca")

        // Simulamos que o Auth falha com exceção de senha fraca
        whenever(mockAuth.createUserWithEmailAndPassword(any(), any()))
            .thenReturn(Tasks.forException(exceptionFirebase))

        // --- AÇÃO ---
        // O teste espera que lance RegisterWeakPasswordException (definido no @Test(expected=...))
        repository.registerUser("user", "email@teste.com", "123")
    }

    @Test(expected = RegisterEmailInUseException::class)
    fun `registerUser deve lancar RegisterEmailInUseException quando email ja existe`() = runBlocking {
        // --- CENÁRIO ---
        val exceptionFirebase = Mockito.mock(FirebaseAuthUserCollisionException::class.java)
        whenever(exceptionFirebase.message).thenReturn("Email já existe")

        // Simulamos colisão de email
        whenever(mockAuth.createUserWithEmailAndPassword(any(), any()))
            .thenReturn(Tasks.forException(exceptionFirebase))

        // --- AÇÃO ---
        repository.registerUser("user", "email@teste.com", "123")
    }

    @Test(expected = RegisterGenericException::class)
    fun `registerUser deve lancar RegisterGenericException quando erro desconhecido`() = runBlocking {
        // --- CENÁRIO ---
        // Erro genérico qualquer
        whenever(mockAuth.createUserWithEmailAndPassword(any(), any()))
            .thenReturn(Tasks.forException(RuntimeException("Erro de rede")))

        // --- AÇÃO ---
        repository.registerUser("user", "email@teste.com", "123")
    }
}