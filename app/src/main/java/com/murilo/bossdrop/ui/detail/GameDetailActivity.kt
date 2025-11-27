package com.murilo.bossdrop.ui.detail

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.murilo.bossdrop.R
import com.murilo.bossdrop.data.model.ItadPromotion
import com.murilo.bossdrop.databinding.ActivityGameDetailBinding
import java.text.NumberFormat
import java.util.Locale

class GameDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameDetailBinding
    private val viewModel: GameDetailViewModel by viewModels()

    private var gameID: String? = null
    private var promotionData: ItadPromotion? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        gameID = intent.getStringExtra("GAME_ID")

        val intentTitle = intent.getStringExtra("GAME_TITLE")
        val intentImage = intent.getStringExtra("GAME_IMAGE")

        if (intentTitle != null) binding.gameTitle.text = intentTitle

        if (intentImage != null) {
            Glide.with(this)
                .load(intentImage)
                .placeholder(R.drawable.ic_store_placeholder)
                .into(binding.gameBanner)
        }

        if (gameID == null) {
            Toast.makeText(this, "Erro: ID do jogo não encontrado", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        viewModel.setInitialData(gameID!!, intentTitle, intentImage)

        setupClickListeners()
        setupObservers()

        if (gameID != null) viewModel.loadDetails(gameID!!)
    }

    private fun setupClickListeners() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.goToOfferButton.setOnClickListener {
            val redirectUrl = promotionData?.deal?.url

            if (redirectUrl.isNullOrEmpty()) {
                Toast.makeText(this, "Link da oferta não disponível.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(redirectUrl))
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "Não foi possível abrir o link.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.favoriteButton.setOnClickListener {
            viewModel.toggleFavorite()
        }
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(this) { isLoading ->
                   }

        viewModel.dealDetails.observe(this) { promotion ->
            if (promotion == null) {
                binding.tvNoOffers.visibility = View.VISIBLE
                binding.discountedPrice.visibility = View.GONE
                binding.originalPrice.visibility = View.GONE
                binding.goToOfferButton.visibility = View.GONE
                binding.storeLogoContainer.visibility = View.GONE

            } else {
                this.promotionData = promotion
                binding.tvNoOffers.visibility = View.GONE
                binding.discountedPrice.visibility = View.VISIBLE
                binding.goToOfferButton.visibility = View.VISIBLE
                binding.storeLogoContainer.visibility = View.VISIBLE

                val gameInfo = promotion
                val dealInfo = promotion.deal

                binding.gameTitle.text = gameInfo.title

                Glide.with(this)
                    .load(gameInfo.assets?.banner600)
                    .placeholder(R.drawable.ic_store_placeholder)
                    .into(binding.gameBanner)

                Glide.with(this)
                    .load(gameInfo.assets?.boxart)
                    .placeholder(R.drawable.ic_store_placeholder)
                    .into(binding.gamePoster)

                val brlFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
                val newPriceAmount = dealInfo?.price?.amount ?: 0.0
                val oldPriceAmount = dealInfo?.regular?.amount ?: 0.0

                binding.discountedPrice.text = if (newPriceAmount == 0.0) {
                    "Grátis"
                } else {
                    brlFormat.format(newPriceAmount)
                }

                if (oldPriceAmount == 0.0 || oldPriceAmount == newPriceAmount) {
                    binding.originalPrice.visibility = View.GONE
                } else {
                    binding.originalPrice.visibility = View.VISIBLE
                    binding.originalPrice.text = brlFormat.format(oldPriceAmount)
                    binding.originalPrice.paintFlags = binding.originalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                }

                binding.storeLogo.setImageResource(getStoreLogo(dealInfo?.shop?.name))
            }
        }

        viewModel.isFavorite.observe(this) { isFavorite ->
            updateFavoriteIcon(isFavorite)
        }
    }

    private fun getStoreLogo(shopName: String?): Int {
        return when (shopName) {
            "Steam" -> R.drawable.steam_logo
            "GOG" -> R.drawable.gog_logo
            "Ubisoft Store" -> R.drawable.ubisoft_store_logo
            "Epic Game Store" -> R.drawable.epic_games_logo
            "Origin" -> R.drawable.origin_logo
            "EA App" -> R.drawable.ea_app_logo
            "Green Man Gaming" -> R.drawable.gmg_logo
            "Nuuvem" -> R.drawable.nuuvem_logo
            "Microsoft Store" -> R.drawable.microsoft_logo
            else -> R.drawable.ic_store_placeholder
        }
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        if (isFavorite) {
            binding.favoriteButton.setImageResource(R.drawable.ic_favorite_filled)
        } else {
            binding.favoriteButton.setImageResource(R.drawable.ic_favorite_border)
        }
    }
}