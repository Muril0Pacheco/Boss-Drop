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
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val viewModel: HomeViewModel by viewModels()

    // O Adapter agora é inicializado com o tipo ItadPromotion
    private val adapter: PromotionAdapter = PromotionAdapter(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupObservers()
        setupBottomNavigation()

        binding.profileIcon.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        // Removemos o clique do searchEditText
        // Vamos fazer isso na SearchActivity

        binding.homeRootLayout.setOnClickListener {
            esconderTeclado()
        }
    }

    private fun setupRecyclerView() {
        binding.promotionsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.promotionsRecyclerView.adapter = adapter
    }

    private fun setupObservers() {
        // Observador de promoções (agora usa ItadPromotion)
        viewModel.promotions.observe(this) { promotions ->
            // Atualiza a lista no adapter existente
            adapter.updateList(promotions)
        }

        // Observador do Loading (agora usa o ID 'progressBar')
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                // Se estiver carregando, mostra o ProgressBar e esconde a lista
                binding.promotionsRecyclerView.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE // ◀️ --- LÓGICA ATIVADA
            } else {
                // Se terminou, esconde o ProgressBar e mostra a lista
                binding.promotionsRecyclerView.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE // ◀️ --- LÓGICA ATIVADA
            }
        }
    }

    private fun setupBottomNavigation() {
        // ◀️ --- LÓGICA DO SEARCH ATUALIZADA ---
        // O clique no EditText vai direto para a SearchActivity
        binding.searchEditText.isFocusable = false
        binding.searchEditText.isClickable = true
        binding.searchEditText.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
            // finish() // Não finalize a Home, deixe o usuário voltar
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Já estamos aqui
                    true
                }
                R.id.navigation_search -> {
                    startActivity(Intent(this, SearchActivity::class.java))
                    // finish() // Não finalize a Home, deixe o usuário voltar
                    true
                }
                R.id.navigation_favorites -> {
                    startActivity(Intent(this, FavoritesActivity::class.java))
                    // finish() // Não finalize a Home, deixe o usuário voltar
                    true
                }
                else -> false
            }
        }
    }
}