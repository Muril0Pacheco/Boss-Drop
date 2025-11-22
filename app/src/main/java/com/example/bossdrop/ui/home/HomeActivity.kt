package com.example.bossdrop.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bossdrop.R
import com.example.bossdrop.adapter.PromotionAdapter
import com.example.bossdrop.data.repository.UserRepository
import com.example.bossdrop.databinding.ActivityHomeBinding
import com.example.bossdrop.ui.detail.GameDetailActivity
import com.example.bossdrop.ui.esconderTeclado
import com.example.bossdrop.ui.favorites.FavoritesActivity
import com.example.bossdrop.ui.search.SearchActivity
import com.example.bossdrop.ui.settings.SettingsActivity
import com.google.android.material.bottomsheet.BottomSheetDialog


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val viewModel: HomeViewModel by viewModels()

    private val adapter: PromotionAdapter = PromotionAdapter(emptyList())
    private val userRepository = UserRepository()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userRepository.updateFcmToken()

        askNotificationPermission()

        checkIntentForNotificationClick()

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

    private fun checkIntentForNotificationClick() {
        val gameId = intent.getStringExtra("gameId") // O campo 'data' do JSON vira extra aqui
        if (gameId != null) {
            val detailIntent = Intent(this, GameDetailActivity::class.java)
            detailIntent.putExtra("GAME_ID", gameId)
            startActivity(detailIntent)
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun setupClickListeners() {
        binding.profileIcon.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        binding.filterIcon.setOnClickListener {
            showFilterDialog()
        }
    }

    private fun showFilterDialog() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_filter, null)
        dialog.setContentView(view)

        val radioGroup = view.findViewById<RadioGroup>(R.id.filterRadioGroup)
        val btnApply = view.findViewById<Button>(R.id.applyButton)

        val idToCheck = when (viewModel.currentFilter) {
            HomeViewModel.FilterType.RECENT -> R.id.rbDefault
            HomeViewModel.FilterType.LOWEST_PRICE -> R.id.rbLowestPrice
            HomeViewModel.FilterType.HIGHEST_DISCOUNT -> R.id.rbHighestDiscount
            HomeViewModel.FilterType.MOST_POPULAR -> R.id.rbMostPopular
        }
        radioGroup.check(idToCheck)

        btnApply.setOnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId

            val filterType = when (selectedId) {
                R.id.rbDefault -> HomeViewModel.FilterType.RECENT
                R.id.rbLowestPrice -> HomeViewModel.FilterType.LOWEST_PRICE
                R.id.rbHighestDiscount -> HomeViewModel.FilterType.HIGHEST_DISCOUNT
                R.id.rbMostPopular -> HomeViewModel.FilterType.MOST_POPULAR
                else -> HomeViewModel.FilterType.RECENT
            }

            viewModel.applyFilter(filterType)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setupRecyclerView() {
        binding.promotionsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.promotionsRecyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.promotions.observe(this) { promotions ->
            adapter.updateList(promotions)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.promotionsRecyclerView.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.promotionsRecyclerView.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun setupBottomNavigation() {
        binding.searchEditText.isFocusable = false
        binding.searchEditText.isClickable = true
        binding.searchEditText.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    true
                }
                R.id.navigation_search -> {
                    startActivity(Intent(this, SearchActivity::class.java))
                    true
                }
                R.id.navigation_favorites -> {
                    startActivity(Intent(this, FavoritesActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}