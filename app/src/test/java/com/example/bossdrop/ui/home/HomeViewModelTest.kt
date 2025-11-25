package com.example.bossdrop.ui.home

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.bossdrop.MainDispatcherRule
import com.example.bossdrop.data.model.ItadPromotion
import com.example.bossdrop.data.model.ItadDealInfo
import com.example.bossdrop.data.model.ItadPrice
import com.example.bossdrop.data.repository.PromotionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    lateinit var mockRepo: PromotionRepository

    private lateinit var mockedLog: MockedStatic<Log>

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        mockedLog = Mockito.mockStatic(Log::class.java)
    }

    @After
    fun tearDown() {
        mockedLog.close()
    }

    @Test
    fun `init deve carregar promocoes e aplicar filtro popular`() = runTest {
        // --- CENÁRIO ---
        // Criamos objetos REAIS (não mocks)
        val promoRank1 = createFakePromotion(rank = 1, title = "Top 1")
        val promoRank2 = createFakePromotion(rank = 2, title = "Top 2")
        val listaDesordenada = listOf(promoRank2, promoRank1)

        whenever(mockRepo.getPromotionsFromFirestore()).thenReturn(listaDesordenada)

        // --- AÇÃO ---
        viewModel = HomeViewModel(mockRepo)

        // --- VERIFICAÇÃO ---
        val result = viewModel.promotions.value
        assertEquals(2, result?.size)

        // Verifica se ordenou pelo Rank (1 vem antes do 2)
        assertEquals(promoRank1, result?.get(0))
        assertEquals(false, viewModel.isLoading.value)
    }

    @Test
    fun `loadPromotions deve tratar erro da API`() = runTest {
        whenever(mockRepo.getPromotionsFromFirestore()).thenThrow(RuntimeException("Erro API"))

        viewModel = HomeViewModel(mockRepo)

        assertEquals(emptyList<ItadPromotion>(), viewModel.promotions.value)
        assertEquals(false, viewModel.isLoading.value)
    }

    @Test
    fun `applyFilter LOWEST_PRICE deve ordenar por preco`() = runTest {
        val pBarato = createFakePromotion(price = 10.0, title = "Barato")
        val pCaro = createFakePromotion(price = 100.0, title = "Caro")
        val lista = listOf(pCaro, pBarato)

        whenever(mockRepo.getPromotionsFromFirestore()).thenReturn(lista)
        viewModel = HomeViewModel(mockRepo)

        // --- AÇÃO ---
        viewModel.applyFilter(HomeViewModel.FilterType.LOWEST_PRICE)

        // --- VERIFICAÇÃO ---
        val result = viewModel.promotions.value
        assertEquals(pBarato, result?.get(0))
    }

    @Test
    fun `applyFilter HIGHEST_DISCOUNT deve ordenar por desconto decrescente`() = runTest {
        val pDescontaco = createFakePromotion(cut = 90, title = "-90%")
        val pDescontoRuim = createFakePromotion(cut = 10, title = "-10%")
        val lista = listOf(pDescontoRuim, pDescontaco)

        whenever(mockRepo.getPromotionsFromFirestore()).thenReturn(lista)
        viewModel = HomeViewModel(mockRepo)

        // --- AÇÃO ---
        viewModel.applyFilter(HomeViewModel.FilterType.HIGHEST_DISCOUNT)

        // --- VERIFICAÇÃO ---
        val result = viewModel.promotions.value
        assertEquals(pDescontaco, result?.get(0))
    }

    // --- HELPER OTIMIZADO ---
    // Cria um objeto real preenchendo apenas o necessário
    private fun createFakePromotion(
        title: String = "Teste",
        price: Double = 0.0,
        cut: Int = 0,
        rank: Int? = null
    ): ItadPromotion {
        return ItadPromotion(
            title = title,
            popularityRank = rank,
            deal = ItadDealInfo(
                price = ItadPrice(amount = price),
                cut = cut
            )
        )
    }
}