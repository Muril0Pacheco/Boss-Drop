package com.example.bossdrop.ui.favorites

import com.example.bossdrop.R
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bossdrop.adapter.FavoritesAdapter
import com.example.bossdrop.databinding.ActivityFavoritesBinding
import com.example.bossdrop.ui.esconderTeclado
import com.example.bossdrop.ui.home.HomeActivity
import com.example.bossdrop.ui.search.SearchActivity


class FavoritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritesBinding
    private val viewModel: FavoritesViewModel by viewModels()
    private lateinit var adapter: FavoritesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.favoriteRootLayout.setOnClickListener {
            esconderTeclado()
        }

        setupRecyclerView()
        setupObservers()
        setupBottomNavigation()
    }

    private fun setupRecyclerView() {
        adapter = FavoritesAdapter(emptyList()) { gameId ->
            val intent = Intent(this, com.example.bossdrop.ui.detail.GameDetailActivity::class.java)
            intent.putExtra("GAME_ID", gameId)
            startActivity(intent)
        }


        binding.favoritesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.favoritesRecyclerView.adapter = adapter
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
