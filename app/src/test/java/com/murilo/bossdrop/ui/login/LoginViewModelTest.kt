package com.murilo.bossdrop.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.murilo.bossdrop.MainDispatcherRule
import com.murilo.bossdrop.data.repository.LoginRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class LoginViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    lateinit var mockRepo: LoginRepository

    @Mock
    lateinit var mockUser: FirebaseUser

    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `init deve navegar para home se usuario ja logado`() {
        whenever(mockRepo.getCurrentUser()).thenReturn(mockUser)

        viewModel = LoginViewModel(mockRepo)

        assertEquals(true, viewModel.navigateToHome.value)
    }

    @Test
    fun `login deve atualizar status e navegar quando sucesso`() {
        whenever(mockRepo.getCurrentUser()).thenReturn(null)

        // CONFIGURAÇÃO DO MOCK COM doAnswer (Mais seguro que Captor)
        // Quando chamar login(qualquer coisa...), execute o bloco:
        doAnswer { invocation ->
            // O 3º argumento (índice 2) é o callback: (Boolean, String?) -> Unit
            val callback = invocation.arguments[2] as (Boolean, String?) -> Unit
            // Chamamos o callback simulando SUCESSO
            callback(true, null)
            null
        }.whenever(mockRepo).login(any(), any(), any())

        viewModel = LoginViewModel(mockRepo)
        viewModel.login("teste@email.com", "123")

        assertEquals(false, viewModel.isLoading.value)
        assertEquals("Login realizado com sucesso!", viewModel.statusMessage.value)
        assertEquals(true, viewModel.navigateToHome.value)
    }

    @Test
    fun `login deve mostrar erro quando falha`() {
        whenever(mockRepo.getCurrentUser()).thenReturn(null)

        // Simulando FALHA
        doAnswer { invocation ->
            val callback = invocation.arguments[2] as (Boolean, String?) -> Unit
            callback(false, "Senha incorreta")
            null
        }.whenever(mockRepo).login(any(), any(), any())

        viewModel = LoginViewModel(mockRepo)
        viewModel.login("teste@email.com", "123")

        assertEquals("Falha no login: Senha incorreta", viewModel.statusMessage.value)
        assertEquals(null, viewModel.navigateToHome.value)
    }

    @Test
    fun `firebaseAuthWithGoogle deve logar com sucesso`() {
        whenever(mockRepo.getCurrentUser()).thenReturn(null)

        // Simulando SUCESSO no Google
        doAnswer { invocation ->
            // O 2º argumento (índice 1) é o callback
            val callback = invocation.arguments[1] as (Boolean, String?) -> Unit
            callback(true, null)
            null
        }.whenever(mockRepo).firebaseAuthWithGoogle(any(), any())

        viewModel = LoginViewModel(mockRepo)
        viewModel.firebaseAuthWithGoogle("token_123")

        assertEquals("Login com Google realizado com sucesso!", viewModel.statusMessage.value)
        assertEquals(true, viewModel.navigateToHome.value)
    }
}