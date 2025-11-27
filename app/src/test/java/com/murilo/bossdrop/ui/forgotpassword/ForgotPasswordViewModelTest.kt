package com.murilo.bossdrop.ui.forgotpassword

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.murilo.bossdrop.MainDispatcherRule
import com.murilo.bossdrop.data.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class ForgotPasswordViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    lateinit var mockRepo: UserRepository

    private lateinit var viewModel: ForgotPasswordViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = ForgotPasswordViewModel(mockRepo)
    }

    @Test
    fun `sendResetLink deve mostrar erro se email for invalido`() = runTest {
        // --- AÇÃO ---
        viewModel.sendResetLink("email_sem_arroba")

        // --- VERIFICAÇÃO ---
        assertEquals("Por favor, insira um email válido.", viewModel.statusMessage.value)

        // Garante que NÃO chamou o repositório
        verify(mockRepo, never()).sendPasswordResetEmail(any())
    }

    @Test
    fun `sendResetLink deve enviar email com sucesso`() = runTest {
        // --- CENÁRIO ---
        val email = "teste@email.com"
        whenever(mockRepo.sendPasswordResetEmail(email)).thenReturn(true)

        // --- AÇÃO ---
        viewModel.sendResetLink(email)

        // --- VERIFICAÇÃO ---
        verify(mockRepo).sendPasswordResetEmail(email)
        assertEquals("Link enviado para $email", viewModel.statusMessage.value)
        assertEquals(false, viewModel.isLoading.value)
    }

    @Test
    fun `sendResetLink deve mostrar erro se falha no envio`() = runTest {
        // --- CENÁRIO ---
        val email = "teste@email.com"
        // Simula falha (ex: usuário não encontrado)
        whenever(mockRepo.sendPasswordResetEmail(email)).thenReturn(false)

        // --- AÇÃO ---
        viewModel.sendResetLink(email)

        // --- VERIFICAÇÃO ---
        assertEquals("Erro ao enviar o link. Tente novamente.", viewModel.statusMessage.value)
        assertEquals(false, viewModel.isLoading.value)
    }
}