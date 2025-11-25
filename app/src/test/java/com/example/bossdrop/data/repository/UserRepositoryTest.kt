package com.example.bossdrop.data.repository

import android.util.Log
import com.example.bossdrop.data.model.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserInfo
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class UserRepositoryTest {

    @Mock
    lateinit var mockAuth: FirebaseAuth
    @Mock
    lateinit var mockFirestore: FirebaseFirestore
    @Mock
    lateinit var mockMessaging: FirebaseMessaging
    @Mock
    lateinit var mockUser: FirebaseUser

    // Mocks do Firestore
    @Mock
    lateinit var mockCollection: CollectionReference
    @Mock
    lateinit var mockDocument: DocumentReference
    @Mock
    lateinit var mockDocSnapshot: DocumentSnapshot

    // Mocks de Tasks e Resultados
    @Mock
    lateinit var mockVoidTask: Task<Void>
    @Mock
    lateinit var mockStringTask: Task<String>

    // Captor para callbacks e dados
    @Captor
    lateinit var stringCallbackCaptor: ArgumentCaptor<OnCompleteListener<String>>

    @Captor
    lateinit var mapCaptor: ArgumentCaptor<Map<String, Any>>

    // Mocks Estáticos
    private lateinit var mockedLog: MockedStatic<Log>
    private lateinit var mockedEmailAuth: MockedStatic<EmailAuthProvider>

    private lateinit var repository: UserRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

        // Mockando classes estáticas
        mockedLog = Mockito.mockStatic(Log::class.java)
        mockedEmailAuth = Mockito.mockStatic(EmailAuthProvider::class.java)

        // PREVENÇÃO DE NULLPOINTER:
        whenever(mockFirestore.collection("users")).thenReturn(mockCollection)
        whenever(mockCollection.document(anyString())).thenReturn(mockDocument)

        // Inicializa repo com as dependências mockadas
        repository = UserRepository(mockAuth, mockFirestore, mockMessaging)
    }

    @After
    fun tearDown() {
        mockedLog.close()
        mockedEmailAuth.close()
    }

    @Test
    fun `getCurrentUser deve retornar usuario com dados mesclados`() {
        runBlocking {
            // --- CENÁRIO ---
            val uid = "123"
            val email = "teste@email.com"
            val fakeUser = User(uid = uid, username = "TestUser")

            whenever(mockAuth.currentUser).thenReturn(mockUser)
            whenever(mockUser.uid).thenReturn(uid)
            whenever(mockUser.email).thenReturn(email)

            val mockUserInfo = Mockito.mock(UserInfo::class.java)
            whenever(mockUserInfo.providerId).thenReturn("password")
            whenever(mockUser.providerData).thenReturn(listOf(mockUserInfo))

            whenever(mockDocument.get()).thenReturn(com.google.android.gms.tasks.Tasks.forResult(mockDocSnapshot))
            whenever(mockDocSnapshot.toObject(User::class.java)).thenReturn(fakeUser)

            // --- AÇÃO ---
            val result = repository.getCurrentUser()

            // --- VERIFICAÇÃO ---
            assertEquals("TestUser", result?.username)
            assertEquals(email, result?.email)
        }
    }

    @Test
    fun `updateUsername deve chamar update no firestore`() {
        runBlocking {
            // --- CENÁRIO ---
            whenever(mockAuth.currentUser).thenReturn(mockUser)
            whenever(mockUser.uid).thenReturn("123")

            whenever(mockDocument.update(eq("username"), anyString()))
                .thenReturn(com.google.android.gms.tasks.Tasks.forResult(null))

            // --- AÇÃO ---
            repository.updateUsername("NovoNome")

            // --- VERIFICAÇÃO ---
            verify(mockDocument).update("username", "NovoNome")
        }
    }

    @Test
    fun `updateEmail deve atualizar no Auth e no Firestore`() {
        runBlocking {
            // --- CENÁRIO ---
            whenever(mockAuth.currentUser).thenReturn(mockUser)
            whenever(mockUser.uid).thenReturn("123")

            // Simula sucessos
            whenever(mockUser.updateEmail(anyString())).thenReturn(com.google.android.gms.tasks.Tasks.forResult(null))
            whenever(mockDocument.update(eq("email"), anyString())).thenReturn(com.google.android.gms.tasks.Tasks.forResult(null))

            // --- AÇÃO ---
            repository.updateEmail("novo@email.com")

            // --- VERIFICAÇÃO ---
            verify(mockUser).updateEmail("novo@email.com")
            verify(mockDocument).update("email", "novo@email.com")
        }
    }

    @Test
    fun `reauthenticateUser deve retornar true quando credenciais validas`() {
        runBlocking {
            // --- CENÁRIO ---
            val senha = "123"
            val email = "teste@teste.com"
            val mockCredential = Mockito.mock(AuthCredential::class.java)

            whenever(mockAuth.currentUser).thenReturn(mockUser)
            whenever(mockUser.email).thenReturn(email)

            mockedEmailAuth.`when`<AuthCredential> {
                EmailAuthProvider.getCredential(email, senha)
            }.thenReturn(mockCredential)

            whenever(mockUser.reauthenticate(mockCredential))
                .thenReturn(com.google.android.gms.tasks.Tasks.forResult(null))

            // --- AÇÃO ---
            val result = repository.reauthenticateUser(senha)

            // --- VERIFICAÇÃO ---
            assertTrue(result)
        }
    }

    @Test
    fun `updateFcmToken deve salvar token se obtido com sucesso`() {
        // --- CENÁRIO ---
        val token = "token_fcm_123"
        whenever(mockAuth.currentUser).thenReturn(mockUser)
        whenever(mockUser.uid).thenReturn("123")

        // Simula a chamada do Token
        whenever(mockMessaging.token).thenReturn(mockStringTask)
        whenever(mockStringTask.isSuccessful).thenReturn(true)
        whenever(mockStringTask.result).thenReturn(token)

        whenever(mockDocument.set(any(), any<com.google.firebase.firestore.SetOptions>()))
            .thenReturn(mockVoidTask)

        // --- AÇÃO ---
        repository.updateFcmToken()

        // --- DISPARAR CALLBACK ---
        verify(mockStringTask).addOnCompleteListener(stringCallbackCaptor.capture())
        stringCallbackCaptor.value.onComplete(mockStringTask)

        // --- VERIFICAÇÃO (Usando Captor para evitar erro de tipos) ---
        // Capturamos o mapa que foi enviado para o .set()
        verify(mockDocument).set(mapCaptor.capture(), any<com.google.firebase.firestore.SetOptions>())

        // Verificamos se dentro desse mapa tem o token correto
        val mapaCapturado = mapCaptor.value
        assertEquals(token, mapaCapturado["fcmToken"])
    }
}