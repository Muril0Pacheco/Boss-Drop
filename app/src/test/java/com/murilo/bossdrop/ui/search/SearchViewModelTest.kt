package com.murilo.bossdrop.ui.search

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.murilo.bossdrop.MainDispatcherRule
import com.murilo.bossdrop.data.model.ItadPromotion
import com.murilo.bossdrop.data.repository.PromotionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class SearchViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    lateinit var mockRepo: PromotionRepository

    private lateinit var mockedLog: MockedStatic<Log>

    private lateinit var viewModel: SearchViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        mockedLog = Mockito.mockStatic(Log::class.java)
        // Não iniciamos a ViewModel aqui para configurar o mock do repo antes do INIT rodar
    }

    @After
    fun tearDown() {
        mockedLog.close()
    }

    @Test
    fun `init deve carregar dados e exibir recomendados ordenados por rank`() = runTest {
        // --- CENÁRIO ---
        // Criamos 3 itens:
        // Rank 2 (deve ficar em segundo)
        val p2 = ItadPromotion(title = "Jogo Rank 2", popularityRank = 2)
        // Rank 1 (deve ficar em primeiro)
        val p1 = ItadPromotion(title = "Jogo Rank 1", popularityRank = 1)
        // Sem Rank (não deve aparecer nos recomendados)
        val pSemRank = ItadPromotion(title = "Jogo Sem Rank", popularityRank = null)

        val listaMisturada = listOf(p2, pSemRank, p1)

        whenever(mockRepo.getPromotionsFromFirestore()).thenReturn(listaMisturada)

        // --- AÇÃO ---
        viewModel = SearchViewModel(mockRepo)

        // --- VERIFICAÇÃO ---
        val listaExibida = viewModel.dealList.value
        assertEquals(2, listaExibida?.size) // O "Sem Rank" deve ser filtrado
        assertEquals("Jogo Rank 1", listaExibida?.get(0)?.title) // Rank 1 primeiro
        assertEquals("Jogo Rank 2", listaExibida?.get(1)?.title) // Rank 2 depois

        assertEquals(false, viewModel.isSearchMode.value)
        assertEquals(false, viewModel.isLoading.value)
    }

    @Test
    fun `loadData deve tratar erro e limpar lista`() = runTest {
        // --- CENÁRIO ---
        whenever(mockRepo.getPromotionsFromFirestore()).thenThrow(RuntimeException("Erro API"))

        // --- AÇÃO ---
        viewModel = SearchViewModel(mockRepo)

        // --- VERIFICAÇÃO ---
        assertTrue(viewModel.dealList.value?.isEmpty() == true)
        assertEquals(false, viewModel.isLoading.value)
    }

    @Test
    fun `searchForGame deve filtrar lista cacheada pelo titulo`() = runTest {
        // --- CENÁRIO ---
        val jogoAlvo = ItadPromotion(title = "Zelda Breath of the Wild")
        val jogoIgnorado = ItadPromotion(title = "Mario Kart")

        whenever(mockRepo.getPromotionsFromFirestore()).thenReturn(listOf(jogoAlvo, jogoIgnorado))

        viewModel = SearchViewModel(mockRepo) // Carrega o cache inicial

        // --- AÇÃO ---
        viewModel.searchForGame("zelda") // Busca minúscula para testar ignoreCase

        // --- VERIFICAÇÃO ---
        val lista = viewModel.dealList.value
        assertEquals(1, lista?.size)
        assertEquals("Zelda Breath of the Wild", lista?.get(0)?.title)
        assertEquals(true, viewModel.isSearchMode.value)
    }

    @Test
    fun `searchForGame com texto vazio deve voltar para recomendados`() = runTest {
        // --- CENÁRIO ---
        val jogoPopular = ItadPromotion(title = "Popular", popularityRank = 1)

        whenever(mockRepo.getPromotionsFromFirestore()).thenReturn(listOf(jogoPopular))
        viewModel = SearchViewModel(mockRepo)

        // Primeiro fazemos uma busca para mudar o estado
        viewModel.searchForGame("xyz")
        assertEquals(true, viewModel.isSearchMode.value)

        // --- AÇÃO ---
        viewModel.searchForGame("") // Limpa a busca

        // --- VERIFICAÇÃO ---
        assertEquals(false, viewModel.isSearchMode.value)
        assertEquals("Popular", viewModel.dealList.value?.get(0)?.title)
    }
}