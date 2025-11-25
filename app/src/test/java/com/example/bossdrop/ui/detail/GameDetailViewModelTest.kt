package com.example.bossdrop.ui.detail

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.bossdrop.MainDispatcherRule
import com.example.bossdrop.data.model.FavoriteItem
import com.example.bossdrop.data.model.ItadPromotion
import com.example.bossdrop.data.repository.FavoriteRepository
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
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull // <--- IMPORTANTE: Adicionado para lidar com nulls
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class GameDetailViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    lateinit var mockPromotionRepo: PromotionRepository

    @Mock
    lateinit var mockFavoriteRepo: FavoriteRepository

    private lateinit var mockedLog: MockedStatic<Log>

    private lateinit var viewModel: GameDetailViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        mockedLog = Mockito.mockStatic(Log::class.java)

        viewModel = GameDetailViewModel(mockPromotionRepo, mockFavoriteRepo)
    }

    @After
    fun tearDown() {
        mockedLog.close()
    }

    @Test
    fun `loadDetails deve carregar dados e verificar favorito`() = runTest {
        val gameId = "game123"
        val fakePromotion = ItadPromotion(title = "Jogo Teste")

        whenever(mockPromotionRepo.getPromotionById(gameId)).thenReturn(fakePromotion)
        whenever(mockFavoriteRepo.getFavorites()).thenReturn(emptyList())

        viewModel.loadDetails(gameId)

        assertEquals("Jogo Teste", viewModel.dealDetails.value?.title)
        assertEquals(false, viewModel.isLoading.value)
        assertEquals(false, viewModel.isFavorite.value)
    }

    @Test
    fun `loadDetails deve marcar favorito como true se jogo estiver na lista`() = runTest {
        val gameId = "game123"
        val fakePromotion = ItadPromotion(title = "Jogo Favorito")
        val fakeFavItem = FavoriteItem(
            gameId = "game123",
            gameTitle = "Jogo Favorito",
            gameImageUrl = null,
            gamePrice = "R$ 10,00",
            gameDiscount = null
        )

        whenever(mockPromotionRepo.getPromotionById(gameId)).thenReturn(fakePromotion)
        whenever(mockFavoriteRepo.getFavorites()).thenReturn(listOf(fakeFavItem))

        viewModel.loadDetails(gameId)

        assertEquals(true, viewModel.isFavorite.value)
    }

    @Test
    fun `loadDetails deve lidar com erro na API`() = runTest {
        val gameId = "game123"

        whenever(mockPromotionRepo.getPromotionById(gameId)).thenThrow(RuntimeException("API Error"))
        whenever(mockFavoriteRepo.getFavorites()).thenReturn(emptyList())

        viewModel.loadDetails(gameId)

        assertEquals(null, viewModel.dealDetails.value)
        assertEquals(false, viewModel.isLoading.value)
    }

    @Test
    fun `toggleFavorite deve adicionar aos favoritos se nao for favorito`() = runTest {
        val gameId = "1"
        whenever(mockFavoriteRepo.getFavorites()).thenReturn(emptyList())
        whenever(mockPromotionRepo.getPromotionById(gameId)).thenReturn(ItadPromotion(title = "Zelda"))

        viewModel.loadDetails(gameId)

        // CORREÇÃO: Usamos anyOrNull() no terceiro argumento (imagem)
        whenever(mockFavoriteRepo.addToFavorites(any(), any(), anyOrNull())).thenReturn(true)

        viewModel.toggleFavorite()

        // Verificamos com imageUrl = null
        verify(mockFavoriteRepo).addToFavorites(gameId = "1", title = "Zelda", imageUrl = null)
        assertEquals(true, viewModel.isFavorite.value)
    }

    @Test
    fun `toggleFavorite deve remover dos favoritos se ja for favorito`() = runTest {
        val gameId = "1"
        val fakeFavItem = FavoriteItem(
            gameId = "1",
            gameTitle = "Zelda",
            gameImageUrl = null,
            gamePrice = "",
            gameDiscount = null
        )
        whenever(mockFavoriteRepo.getFavorites()).thenReturn(listOf(fakeFavItem))
        whenever(mockPromotionRepo.getPromotionById(gameId)).thenReturn(ItadPromotion(title = "Zelda"))

        viewModel.loadDetails(gameId)

        whenever(mockFavoriteRepo.removeFromFavorites(gameId)).thenReturn(true)

        viewModel.toggleFavorite()

        verify(mockFavoriteRepo).removeFromFavorites("1")
        assertEquals(false, viewModel.isFavorite.value)
    }

    @Test
    fun `toggleFavorite deve usar fallbackTitle se dealDetails for nulo`() = runTest {
        val gameId = "10"
        val fallbackTitle = "Titulo Reserva"

        viewModel.setInitialData(gameId, fallbackTitle, null)

        whenever(mockFavoriteRepo.getFavorites()).thenReturn(emptyList())

        // CORREÇÃO: Usamos anyOrNull() aqui também
        whenever(mockFavoriteRepo.addToFavorites(any(), any(), anyOrNull())).thenReturn(true)

        viewModel.toggleFavorite()

        verify(mockFavoriteRepo).addToFavorites(gameId = "10", title = "Titulo Reserva", imageUrl = null)
        assertEquals(true, viewModel.isFavorite.value)
    }
}