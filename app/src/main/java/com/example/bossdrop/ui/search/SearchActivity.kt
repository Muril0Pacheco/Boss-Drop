package com.example.bossdrop.ui.search

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bossdrop.R
import com.example.bossdrop.adapter.SearchHistoryAdapter
import com.example.bossdrop.adapter.RecommendedAdapter
import com.example.bossdrop.databinding.ActivitySearchBinding
import com.example.bossdrop.ui.esconderTeclado
import com.example.bossdrop.ui.favorites.FavoritesActivity
import com.example.bossdrop.ui.home.HomeActivity

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private val viewModel: SearchViewModel by viewModels() // 🔹 MVVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.searchRootLayout.setOnClickListener {
            esconderTeclado()
        }

        // 1. Listener para o conteúdo que rola (o LinearLayout)
        binding.scrollableContentLayout.setOnClickListener {
            esconderTeclado()
        }

        // 2. Listener para o RecyclerView (se clicar no fundo dele)
        binding.searchHistoryRecyclerView.setOnClickListener {
            esconderTeclado()
        }

        // 3. Listener para o outro RecyclerView
        binding.recommendedRecyclerView.setOnClickListener {
            esconderTeclado()
        }

        setupObservers()
        setupBottomNavigation()
    }

    // Observa os dados vindos do ViewModel
    private fun setupObservers() {
        viewModel.searchHistory.observe(this) { queries ->
            binding.searchHistoryRecyclerView.apply {
                layoutManager = LinearLayoutManager(this@SearchActivity)
                adapter = SearchHistoryAdapter(queries)
            }
        }

        viewModel.recommendedCount.observe(this) { count ->
            binding.recommendedRecyclerView.apply {
                layoutManager = GridLayoutManager(this@SearchActivity, 2)
                adapter = RecommendedAdapter(count)
            }
        }
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
