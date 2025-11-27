package com.murilo.bossdrop.ui.account

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.murilo.bossdrop.MainDispatcherRule
import com.murilo.bossdrop.data.model.User
import com.murilo.bossdrop.data.repository.EmailUpdateException
import com.murilo.bossdrop.data.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class AccountViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    lateinit var mockRepo: UserRepository

    // Mock Estático apenas para o Log
    private lateinit var mockedLog: MockedStatic<Log>

    private lateinit var viewModel: AccountViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

        // Mock do Log
        mockedLog = Mockito.mockStatic(Log::class.java)

        // Mock padrão do repositório
        val userPadrao = User(uid = "123", username = "Antigo", email = "antigo@email.com", providerId = "password")

        runBlocking {
            whenever(mockRepo.getCurrentUser()).thenReturn(userPadrao)
        }

        viewModel = AccountViewModel(mockRepo)
    }

    @After
    fun tearDown() {
        mockedLog.close()
    }

    @Test
    fun `init deve carregar detalhes do usuario`() = runTest {
        assertEquals("Antigo", viewModel.currentUser.value?.username)
    }

    @Test
    fun `saveChanges deve atualizar apenas username se usuario for Google`() = runTest {
        // --- CENÁRIO: Usuário Google ---
        val googleUser = User(uid = "123", username = "Antigo", providerId = "google.com")

        whenever(mockRepo.getCurrentUser()).thenReturn(googleUser)
        viewModel.loadCurrentUserDetails()

        // --- AÇÃO ---
        viewModel.saveChanges("NovoNome", "novo@email.com", "NovaSenha", "SenhaAtual")

        // --- VERIFICAÇÃO ---
        verify(mockRepo).updateUsername("NovoNome")

        verify(mockRepo, never()).updateEmail(any())
        verify(mockRepo, never()).updatePassword(any())

        assertEquals(SaveResult.SUCCESS, viewModel.saveResult.value?.first)
    }

    @Test
    fun `saveChanges deve retornar erro se senha atual vazia (usuario senha)`() = runTest {
        viewModel.saveChanges("Novo", "novo@email.com", "NovaSenha", "") // Senha atual vazia

        assertEquals(SaveResult.ERROR_WRONG_PASSWORD, viewModel.saveResult.value?.first)
        verify(mockRepo, never()).updateUsername(any())
    }

    @Test
    fun `saveChanges deve retornar erro se email invalido`() = runTest {
        // O Regex interno vai validar isso agora, sem precisar de mocks
        viewModel.saveChanges("Novo", "email_ruim_sem_arroba", "NovaSenha", "123")

        assertEquals(SaveResult.ERROR_INVALID_EMAIL, viewModel.saveResult.value?.first)
    }

    @Test
    fun `saveChanges deve atualizar tudo se reautenticacao funcionar`() = runTest {
        whenever(mockRepo.reauthenticateUser("123")).thenReturn(true)

        viewModel.saveChanges("NovoNome", "novo@email.com", "NovaSenha", "123")

        verify(mockRepo).reauthenticateUser("123")
        verify(mockRepo).updateUsername("NovoNome")
        verify(mockRepo).updateEmail("novo@email.com")
        verify(mockRepo).updatePassword("NovaSenha")

        assertEquals(SaveResult.SUCCESS, viewModel.saveResult.value?.first)
    }

    @Test
    fun `saveChanges deve tratar erro de email duplicado`() = runTest {
        whenever(mockRepo.reauthenticateUser(any())).thenReturn(true)

        whenever(mockRepo.updateEmail(any())).thenThrow(EmailUpdateException("EMAIL_ALREADY_IN_USE"))

        viewModel.saveChanges("Novo", "novo@email.com", "", "123")

        assertEquals(SaveResult.ERROR_EMAIL_IN_USE, viewModel.saveResult.value?.first)
    }

    @Test
    fun `onForgotPasswordClicked deve enviar email`() = runTest {
        whenever(mockRepo.sendPasswordResetEmail(any())).thenReturn(true)

        viewModel.onForgotPasswordClicked()

        verify(mockRepo).sendPasswordResetEmail("antigo@email.com")
    }
}