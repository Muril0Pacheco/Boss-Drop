package com.example.bossdrop.ui.home

import android.content.Intent
import com.example.bossdrop.R
import com.example.bossdrop.ui.favorites.FavoritesActivity
import com.example.bossdrop.ui.search.SearchActivity
import com.example.bossdrop.ui.esconderTeclado
import com.example.bossdrop.adapter.PromotionAdapter
import com.example.bossdrop.databinding.ActivityHomeBinding
import com.example.bossdrop.ui.settings.SettingsActivity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var adapter: PromotionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupObservers()
        setupBottomNavigation()

        // Clique do ícone de perfil (adicionado aqui)
        binding.profileIcon.setOnClickListener {
            // 1. Cria a "intenção" de navegar para a SettingsActivity
            val intent = Intent(this, SettingsActivity::class.java)

            // 2. Inicia a nova tela
            startActivity(intent)
        }

        // Carrega os dados ao abrir a tela
        viewModel.loadPromotions()

        binding.homeRootLayout.setOnClickListener {
            esconderTeclado()
        }
    }

    private fun setupRecyclerView() {
        adapter = PromotionAdapter(emptyList())
        binding.promotionsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.promotionsRecyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.promotions.observe(this) { promotions ->
            adapter = PromotionAdapter(promotions)
            binding.promotionsRecyclerView.adapter = adapter
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Já estamos aqui
                    true
                }
                R.id.navigation_search -> {
                    startActivity(Intent(this, SearchActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_favorites -> {
                    // Navega para a tela de Favoritos
                    val intent = Intent(this, FavoritesActivity::class.java)
                    startActivity(intent)
                    finish() // Fecha a tela atual
                    true
                }
                else -> false
            }
        }
    }

}
