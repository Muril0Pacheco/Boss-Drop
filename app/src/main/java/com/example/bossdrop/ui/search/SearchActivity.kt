package com.example.bossdrop.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.bossdrop.R
// ◀️ --- SearchHistoryAdapter REMOVIDO ---
import com.example.bossdrop.adapter.RecommendedAdapter // <-- Adapter correto
import com.example.bossdrop.databinding.ActivitySearchBinding
import com.example.bossdrop.ui.esconderTeclado
import com.example.bossdrop.ui.favorites.FavoritesActivity
import com.example.bossdrop.ui.home.HomeActivity

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private val viewModel: SearchViewModel by viewModels()

    // Usamos apenas o RecommendedAdapter
    private val recommendedAdapter = RecommendedAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerViews()
        setupObservers()
        setupBottomNavigation()
        setupSearchFocus()
        setupSearchListener()
    }

    private fun setupRecyclerViews() {
        // Removemos o historyAdapter

        // Configura o adapter de "Recomendados" (que também exibe a busca)
        binding.recommendedRecyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.recommendedRecyclerView.adapter = recommendedAdapter
    }

    private fun setupObservers() {
        // Observador da LISTA (Recomendados ou Busca)
        viewModel.dealList.observe(this) { deals ->
            recommendedAdapter.updateList(deals)

            // Lógica para mostrar "Nenhum resultado"
            val isSearching = viewModel.isSearchMode.value ?: false
            if (deals.isEmpty() && isSearching) {
                // Se buscou e não achou nada
                binding.tvNoResults.visibility = View.VISIBLE
                binding.scrollableContentLayout.visibility = View.GONE
            } else {
                // Se achou, ou se está nos Recomendados
                binding.tvNoResults.visibility = View.GONE
                binding.scrollableContentLayout.visibility = View.VISIBLE
            }
        }

        // Observador do MODO (Busca vs. Recomendados)
        viewModel.isSearchMode.observe(this) { isSearching ->
            if (isSearching) {
                binding.tvRecomendados.text = "Resultados da Busca:"
            } else {
                binding.tvRecomendados.text = "Recomendados:"
            }
        }

        // Observador do LOADING
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.scrollableContentLayout.visibility = View.GONE
                binding.tvNoResults.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
                // O observador 'dealList' decide se mostra o conteúdo ou o 'tvNoResults'
            }
        }
    }

    // (NOVO) Configura o listener do "Enter" no teclado
    private fun setupSearchListener() {
        binding.searchEditText.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = textView.text.toString()
                esconderTeclado()
                viewModel.searchForGame(query) // Chama a busca no ViewModel
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }

    // (NOVO) Foca no EditText e abre o teclado ao entrar na tela
    private fun setupSearchFocus() {
        binding.searchEditText.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.searchEditText, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.navigation_search
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_favorites -> {
                    startActivity(Intent(this, FavoritesActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}