package com.example.bossdrop.ui.home

import android.content.Intent
import com.example.bossdrop.R
import com.example.bossdrop.ui.favorites.FavoritesActivity
import com.example.bossdrop.ui.search.SearchActivity
import com.example.bossdrop.ui.esconderTeclado
import com.example.bossdrop.adapter.PromotionAdapter
import com.example.bossdrop.databinding.ActivityHomeBinding
import com.example.bossdrop.ui.settings.SettingsActivity
import com.example.bossdrop.ui.home.HomeViewModel.FilterType
import android.widget.Button

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val viewModel: HomeViewModel by viewModels()

    private val adapter: PromotionAdapter = PromotionAdapter(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        setupBottomNavigation()

        binding.profileIcon.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }


        binding.homeRootLayout.setOnClickListener {
            esconderTeclado()
        }
    }

    private fun setupClickListeners() {
        binding.profileIcon.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        // NOVO: Listener do ícone de filtro
        binding.filterIcon.setOnClickListener {
            showFilterDialog()
        }
    }

    private fun showFilterDialog() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_filter, null)
        dialog.setContentView(view)

        // Encontra as views no layout do BottomSheet
        val radioGroup = view.findViewById<RadioGroup>(R.id.filterRadioGroup)
        val btnApply = view.findViewById<Button>(R.id.applyButton)

        btnApply.setOnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId

            // Mapeia o ID do RadioButton selecionado para o Enum FilterType
            val filterType = when (selectedId) {
                R.id.rbDefault -> HomeViewModel.FilterType.RECENT // MUDOU DE DEFAULT PARA RECENT
                R.id.rbLowestPrice -> HomeViewModel.FilterType.LOWEST_PRICE
                R.id.rbHighestDiscount -> HomeViewModel.FilterType.HIGHEST_DISCOUNT
                R.id.rbMostPopular -> HomeViewModel.FilterType.MOST_POPULAR
                else -> HomeViewModel.FilterType.RECENT // MUDOU DE DEFAULT PARA RECENT
            }

            // Chama a lógica de filtro no ViewModel
            viewModel.applyFilter(filterType)

            dialog.dismiss() // Fecha o menu
        }

        dialog.show()
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
                binding.progressBar.visibility = View.VISIBLE
            } else {
                // Se terminou, esconde o ProgressBar e mostra a lista
                binding.promotionsRecyclerView.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun setupBottomNavigation() {
        // O clique no EditText vai direto para a SearchActivity
        binding.searchEditText.isFocusable = false
        binding.searchEditText.isClickable = true
        binding.searchEditText.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
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