package com.example.bossdrop.ui.detail

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.bossdrop.R
import com.example.bossdrop.data.model.ItadPromotion
import com.example.bossdrop.databinding.ActivityGameDetailBinding
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

        // Pega o GAME_ID que foi passado pelo Adapter
        gameID = intent.getStringExtra("GAME_ID")

        if (gameID == null) {
            Toast.makeText(this, "Erro: ID do jogo não encontrado", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setupClickListeners()
        setupObservers()

        viewModel.loadDetailsFromFirestore(gameID!!)
    }
    private fun setupClickListeners() {
        binding.backButton.setOnClickListener {
            finish() // Botão "Voltar"
        }

        binding.goToOfferButton.setOnClickListener {
            // Pega a URL que guardamos nos dados da promoção
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
            // TODO: Adicionar lógica do Firestore aqui
            Toast.makeText(this, "Lógica de Favorito (Firestore) ainda não implementada.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.dealDetails.observe(this) { promotion ->
            if (promotion == null) {
                Toast.makeText(this, "Não foi possível carregar os detalhes.", Toast.LENGTH_SHORT).show()
                return@observe
            }

            this.promotionData = promotion

            // Popula a UI com os dados do Firestore
            val gameInfo = promotion
            val dealInfo = promotion.deal

            binding.gameTitle.text = gameInfo.title

            // Formata os preços para BRL
            val brlFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

            binding.discountedPrice.text = brlFormat.format(dealInfo?.price?.amount ?: 0.0)

            binding.originalPrice.text = brlFormat.format(dealInfo?.regular?.amount ?: 0.0)
            binding.originalPrice.paintFlags = binding.originalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

            // Carrega a imagem do header
            Glide.with(this)
                .load(gameInfo.assets?.boxart ?: gameInfo.assets?.banner600)
                .placeholder(R.drawable.ic_store_placeholder)
                .into(binding.gameBanner)

            // Define o logo da loja
            binding.storeLogo.setImageResource(getStoreLogo(dealInfo?.shop?.name))
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
            else -> R.drawable.ic_store_placeholder
        }
    }
}