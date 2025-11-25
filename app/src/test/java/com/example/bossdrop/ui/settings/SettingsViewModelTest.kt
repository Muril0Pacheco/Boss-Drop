package com.example.bossdrop.ui.settings

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.bossdrop.MainDispatcherRule
import com.example.bossdrop.data.model.User
import com.example.bossdrop.data.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class SettingsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    lateinit var mockRepo: UserRepository

    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        // Não inicializamos a ViewModel aqui para poder configurar o mock do repo antes do INIT
    }

    @Test
    fun `init deve carregar username do usuario logado`() = runTest {
        // --- CENÁRIO ---
        val fakeUser = User(uid = "123", username = "GamerPro")

        // Ensinamos o repo a retornar esse usuário
        whenever(mockRepo.getCurrentUser()).thenReturn(fakeUser)

        // --- AÇÃO ---
        viewModel = SettingsViewModel(mockRepo)

        // --- VERIFICAÇÃO ---
        assertEquals("GamerPro", viewModel.username.value)
    }

    @Test
    fun `init deve exibir fallback se usuario nao encontrado`() = runTest {
        // --- CENÁRIO ---
        whenever(mockRepo.getCurrentUser()).thenReturn(null)

        // --- AÇÃO ---
        viewModel = SettingsViewModel(mockRepo)

        // --- VERIFICAÇÃO ---
        assertEquals("@usuario_nao_encontrado", viewModel.username.value)
    }

    @Test
    fun `onAccountClicked deve navegar para conta`() {
        // Setup básico (precisa retornar algo no init para não quebrar)
        runBlocking { whenever(mockRepo.getCurrentUser()).thenReturn(null) }
        viewModel = SettingsViewModel(mockRepo)

        viewModel.onAccountClicked()

        assertEquals(SettingsNavigation.TO_ACCOUNT, viewModel.navigationEvent.value)
    }

    @Test
    fun `onNotificationsClicked deve navegar para notificacoes`() {
        runBlocking { whenever(mockRepo.getCurrentUser()).thenReturn(null) }
        viewModel = SettingsViewModel(mockRepo)

        viewModel.onNotificationsClicked()

        assertEquals(SettingsNavigation.TO_NOTIFICATIONS, viewModel.navigationEvent.value)
    }

    @Test
    fun `onPrivacyClicked deve navegar para privacidade`() {
        runBlocking { whenever(mockRepo.getCurrentUser()).thenReturn(null) }
        viewModel = SettingsViewModel(mockRepo)

        viewModel.onPrivacyClicked()

        assertEquals(SettingsNavigation.TO_PRIVACY, viewModel.navigationEvent.value)
    }

    @Test
    fun `onHelpClicked deve navegar para ajuda`() {
        runBlocking { whenever(mockRepo.getCurrentUser()).thenReturn(null) }
        viewModel = SettingsViewModel(mockRepo)

        viewModel.onHelpClicked()

        assertEquals(SettingsNavigation.TO_HELP, viewModel.navigationEvent.value)
    }

    @Test
    fun `onSignOutClicked deve disparar evento de logout`() {
        runBlocking { whenever(mockRepo.getCurrentUser()).thenReturn(null) }
        viewModel = SettingsViewModel(mockRepo)

        viewModel.onSignOutClicked()

        assertEquals(true, viewModel.signOutEvent.value)
    }
}