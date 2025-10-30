package com.example.bossdrop.ui.home

import android.content.Intent
import com.example.bossdrop.R
import com.example.bossdrop.ui.favorites.FavoritesActivity
import com.example.bossdrop.ui.search.SearchActivity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bossdrop.adapter.PromotionAdapter
import com.example.bossdrop.databinding.ActivityHomeBinding

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

        // Clique do ícone de perfil (adicionado aqui)
        binding.profileIcon.setOnClickListener {
            Toast.makeText(this, "Abrindo opções de conta...", Toast.LENGTH_SHORT).show()
            // Exemplo para abrir telas configs: startActivity(Intent(this, ProfileActivity::class.java))
        }


        // Carrega os dados ao abrir a tela
        viewModel.loadPromotions()
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
