package com.murilo.bossdrop.ui.favorites

import com.murilo.bossdrop.R
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.murilo.bossdrop.adapter.FavoritesAdapter
import com.murilo.bossdrop.data.repository.FavoriteRepository
import com.murilo.bossdrop.data.repository.GameSearchRepository
import com.murilo.bossdrop.databinding.ActivityFavoritesBinding
import com.murilo.bossdrop.ui.detail.GameDetailActivity
import com.murilo.bossdrop.ui.esconderTeclado
import com.murilo.bossdrop.ui.home.HomeActivity
import com.murilo.bossdrop.ui.search.SearchActivity
import kotlinx.coroutines.launch
import android.view.inputmethod.EditorInfo
import com.murilo.bossdrop.adapter.SearchResultsAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView

class FavoritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritesBinding
    private val viewModel: FavoritesViewModel by viewModels()
    private lateinit var adapter: FavoritesAdapter
    private val searchRepository = GameSearchRepository()
    private val favoriteRepository = FavoriteRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.favoriteRootLayout.setOnClickListener {
            esconderTeclado()
        }

        setupRecyclerView()
        setupObservers()
        setupListeners()
        setupBottomNavigation()
    }

    private fun setupRecyclerView() {
        adapter = FavoritesAdapter(emptyList()) { favoriteItem ->
            val intent = Intent(this, GameDetailActivity::class.java)

            intent.putExtra("GAME_ID", favoriteItem.gameId)
            intent.putExtra("GAME_TITLE", favoriteItem.gameTitle)
            intent.putExtra("GAME_IMAGE", favoriteItem.gameImageUrl)

            startActivity(intent)
        }


        binding.favoritesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.favoritesRecyclerView.adapter = adapter
    }

    private fun setupListeners() {
        binding.fabAddGame.setOnClickListener {
            // MUDANÇA: Verifica o limite ANTES de abrir o diálogo
            if (canAddMoreFavorites()) {
                showSearchDialog()
            } else {
                Toast.makeText(
                    this,
                    "Sua lista de desejos está cheia! O limite é de 20 jogos.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    private fun canAddMoreFavorites(): Boolean {
        // Pega o tamanho da lista atual do ViewModel (ou 0 se for nula)
        val currentCount = viewModel.favorites.value?.size ?: 0
        return currentCount < 20
    }

    private fun showSearchDialog() {
        // 1. Inflar o layout do BottomSheet
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_add_game, null)
        bottomSheetDialog.setContentView(view)

        // 2. Pegar referências dos componentes
        val etSearch = view.findViewById<EditText>(R.id.etSearchGame)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBarSearch)
        val rvResults = view.findViewById<RecyclerView>(R.id.rvSearchResults)

        // 3. Configurar o RecyclerView com o Adapter vazio inicialmente
        rvResults.layoutManager = LinearLayoutManager(this)
        val searchAdapter = SearchResultsAdapter(emptyList()) { selectedGame ->
            // Ação ao clicar num jogo da lista
            addGameToFavorites(selectedGame)
            bottomSheetDialog.dismiss()
        }
        rvResults.adapter = searchAdapter

        // 4. Configurar o Listener de Pesquisa do Teclado
        etSearch.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = v.text.toString()
                if (query.isNotBlank()) {
                    // Esconde teclado (opcional, use sua util fun se tiver)
                    // Realiza a busca
                    performVisualSearch(query, progressBar, searchAdapter)
                }
                return@setOnEditorActionListener true
            }
            false
        }

        bottomSheetDialog.show()
    }

    private fun performVisualSearch(
        query: String,
        progressBar: ProgressBar,
        adapter: SearchResultsAdapter
    ) {
        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE

            // Chama o repositório (Cloud Function)
            val results = searchRepository.searchGlobalGames(query)

            progressBar.visibility = View.GONE

            if (results.isNotEmpty()) {
                adapter.updateList(results)
            } else {
                adapter.updateList(emptyList())
                Toast.makeText(this@FavoritesActivity, "Nenhum jogo encontrado.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Função final de adicionar (já tínhamos algo parecido, só ajustar o parâmetro)
    private fun addGameToFavorites(game: com.murilo.bossdrop.data.repository.SearchResult) {
        lifecycleScope.launch {
            val success = favoriteRepository.addToFavorites(game.id, game.title, game.imageUrl)
            if (success) {
                Toast.makeText(this@FavoritesActivity, "${game.title} adicionado!", Toast.LENGTH_SHORT).show()
                viewModel.loadFavorites()
            } else {
                Toast.makeText(this@FavoritesActivity, "Erro ao salvar.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun performGlobalSearch(query: String) {
        lifecycleScope.launch {
            Toast.makeText(this@FavoritesActivity, "Buscando na nuvem...", Toast.LENGTH_SHORT).show()

            val results = searchRepository.searchGlobalGames(query)

            if (results.isNotEmpty()) {
                val game = results[0]

                val success = favoriteRepository.addToFavorites(
                    game.id,
                    game.title,
                    game.imageUrl
                )

                if (success) {
                    Toast.makeText(this@FavoritesActivity, "${game.title} adicionado!", Toast.LENGTH_LONG).show()
                    viewModel.loadFavorites()
                } else {
                    Toast.makeText(this@FavoritesActivity, "Erro ao salvar favorito.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@FavoritesActivity, "Nenhum jogo encontrado.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupObservers() {
        viewModel.favorites.observe(this) { list ->
            adapter.updateList(list)
        }

        // Novo: Observer para o carregamento
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.navigation_favorites
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_search -> {
                    startActivity(Intent(this, SearchActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_favorites -> true
                else -> false
            }
        }
    }
}
