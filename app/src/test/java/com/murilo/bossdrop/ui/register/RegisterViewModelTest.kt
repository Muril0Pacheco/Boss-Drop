package com.murilo.bossdrop.ui.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.murilo.bossdrop.MainDispatcherRule
import com.murilo.bossdrop.data.repository.RegisterEmailInUseException
import com.murilo.bossdrop.data.repository.RegisterGenericException
import com.murilo.bossdrop.data.repository.RegisterRepository
import com.murilo.bossdrop.data.repository.RegisterWeakPasswordException
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
class RegisterViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    lateinit var mockRepo: RegisterRepository

    private lateinit var viewModel: RegisterViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = RegisterViewModel(mockRepo)
    }

    @Test
    fun `register deve retornar erro se senhas nao coincidem`() = runTest {
        // --- AÇÃO ---
        viewModel.register("user", "email@teste.com", "123456", "senha_diferente")

        // --- VERIFICAÇÃO ---
        val result = viewModel.registrationResult.value
        assertEquals(RegisterResultType.ERROR_WEAK_PASSWORD, result?.first)
        assertEquals("As senhas não coincidem", result?.second)

        // Garante que nem chamou o repo
        verify(mockRepo, never()).registerUser(any(), any(), any())
    }

    @Test
    fun `register deve retornar erro se senha curta`() = runTest {
        // --- AÇÃO ---
        viewModel.register("user", "email@teste.com", "123", "123") // Senha curta

        // --- VERIFICAÇÃO ---
        val result = viewModel.registrationResult.value
        assertEquals(RegisterResultType.ERROR_WEAK_PASSWORD, result?.first)
        assertEquals("A senha deve ter pelo menos 6 caracteres", result?.second)

        verify(mockRepo, never()).registerUser(any(), any(), any())
    }

    @Test
    fun `register deve retornar sucesso quando repositorio funcionar`() = runTest {
        // --- CENÁRIO ---
        // Repo não faz nada (sucesso retorna Unit)
        whenever(mockRepo.registerUser(any(), any(), any())).thenReturn(Unit)

        // --- AÇÃO ---
        viewModel.register("user", "email@teste.com", "123456", "123456")

        // --- VERIFICAÇÃO ---
        verify(mockRepo).registerUser("user", "email@teste.com", "123456")

        assertEquals(RegisterResultType.SUCCESS, viewModel.registrationResult.value?.first)
        assertEquals(false, viewModel.isLoading.value)
    }

    @Test
    fun `register deve retornar erro de senha fraca vindo do repositorio`() = runTest {
        // --- CENÁRIO ---
        // Repo lança erro de senha fraca (ex: falta letra maiúscula)
        whenever(mockRepo.registerUser(any(), any(), any()))
            .thenThrow(RegisterWeakPasswordException("Senha muito simples"))

        // --- AÇÃO ---
        viewModel.register("user", "email@teste.com", "123456", "123456")

        // --- VERIFICAÇÃO ---
        val result = viewModel.registrationResult.value
        assertEquals(RegisterResultType.ERROR_WEAK_PASSWORD, result?.first)
        assertEquals("Senha muito simples", result?.second)
        assertEquals(false, viewModel.isLoading.value)
    }

    @Test
    fun `register deve retornar erro de email em uso`() = runTest {
        // --- CENÁRIO ---
        whenever(mockRepo.registerUser(any(), any(), any()))
            .thenThrow(RegisterEmailInUseException("Email já existe"))

        // --- AÇÃO ---
        viewModel.register("user", "email@teste.com", "123456", "123456")

        // --- VERIFICAÇÃO ---
        val result = viewModel.registrationResult.value
        assertEquals(RegisterResultType.ERROR_EMAIL_IN_USE, result?.first)
        assertEquals(false, viewModel.isLoading.value)
    }

    @Test
    fun `register deve retornar erro generico`() = runTest {
        // --- CENÁRIO ---
        whenever(mockRepo.registerUser(any(), any(), any()))
            .thenThrow(RegisterGenericException("Erro desconhecido"))

        // --- AÇÃO ---
        viewModel.register("user", "email@teste.com", "123456", "123456")

        // --- VERIFICAÇÃO ---
        val result = viewModel.registrationResult.value
        assertEquals(RegisterResultType.ERROR_GENERIC, result?.first)
        assertEquals("Erro desconhecido", result?.second)
    }
}