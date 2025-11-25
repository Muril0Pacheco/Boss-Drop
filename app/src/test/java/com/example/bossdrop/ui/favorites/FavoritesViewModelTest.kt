package com.example.bossdrop.ui.favorites

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.bossdrop.MainDispatcherRule
import com.example.bossdrop.data.model.FavoriteItem
import com.example.bossdrop.data.repository.FavoriteRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class FavoritesViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    lateinit var mockRepo: FavoriteRepository

    private lateinit var viewModel: FavoritesViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `init deve carregar lista de favoritos com sucesso`() = runTest {
        // --- CENÁRIO ---
        val fakeItem = FavoriteItem(
            gameId = "1",
            gameTitle = "Mario",
            gameImageUrl = null,
            gamePrice = "R$ 100",
            gameDiscount = null
        )
        val listaFake = listOf(fakeItem)

        whenever(mockRepo.getFavorites()).thenReturn(listaFake)


        viewModel = FavoritesViewModel(mockRepo)

        assertEquals(1, viewModel.favorites.value?.size)
        assertEquals("Mario", viewModel.favorites.value?.first()?.gameTitle)
        assertEquals(false, viewModel.isLoading.value)
    }

    @Test
    fun `loadFavorites deve lidar com lista vazia`() = runTest {
        whenever(mockRepo.getFavorites()).thenReturn(emptyList())

        viewModel = FavoritesViewModel(mockRepo)

        assertEquals(0, viewModel.favorites.value?.size)
        assertEquals(false, viewModel.isLoading.value)
    }

    @Test
    fun `loadFavorites deve tratar erro e nao crashar`() = runTest {

        whenever(mockRepo.getFavorites()).thenThrow(RuntimeException("Falha no Firebase"))


        viewModel = FavoritesViewModel(mockRepo)


        assertEquals(false, viewModel.isLoading.value)

        assertEquals(0, viewModel.favorites.value?.size ?: 0)
    }
}