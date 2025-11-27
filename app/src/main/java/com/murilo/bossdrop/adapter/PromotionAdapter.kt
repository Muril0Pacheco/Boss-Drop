package com.murilo.bossdrop.adapter

import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.murilo.bossdrop.R
import com.murilo.bossdrop.data.model.ItadPromotion
import com.murilo.bossdrop.databinding.ListItemPromotionBinding
import com.murilo.bossdrop.ui.detail.GameDetailActivity
import java.text.NumberFormat
import java.util.Locale

class PromotionAdapter(
    private var promotions: List<ItadPromotion> = emptyList()
) : RecyclerView.Adapter<PromotionAdapter.PromotionViewHolder>() {

    inner class PromotionViewHolder(val binding: ListItemPromotionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromotionViewHolder {
        val binding = ListItemPromotionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PromotionViewHolder(binding)
    }

    override fun getItemCount() = promotions.size

    override fun onBindViewHolder(holder: PromotionViewHolder, position: Int) {
        val promotion = promotions[position]
        val context = holder.itemView.context

        val brlFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

        holder.binding.apply {
            val title = promotion.title
            val deal = promotion.deal
            val assets = promotion.assets
            val shopName = deal?.shop?.name

            gameTitleTextView.text = title
            val newPriceAmount = deal?.price?.amount ?: 0.0
            val oldPriceAmount = deal?.regular?.amount ?: 0.0
            val discountPercent = deal?.cut ?: 0

            newPriceTextView.text = if (newPriceAmount == 0.0) {
                "Grátis"
            } else {
                brlFormat.format(newPriceAmount)
            }

            if (oldPriceAmount == 0.0 || oldPriceAmount == newPriceAmount) {
                oldPriceTextView.visibility = View.GONE
            } else {
                oldPriceTextView.visibility = View.VISIBLE
                oldPriceTextView.text = brlFormat.format(oldPriceAmount)
                oldPriceTextView.paintFlags = oldPriceTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }

            if (discountPercent == 0) {
                discountTextView.visibility = View.GONE
            } else {
                discountTextView.visibility = View.VISIBLE
                discountTextView.text = "-$discountPercent%"
            }
            Glide.with(context)
                .load(assets?.boxart)
                .placeholder(R.drawable.ic_image_placeholder) // Placeholder
                .into(gameImageView)

            storeLogoImageView.setImageResource(getStoreLogo(shopName))

            holder.itemView.setOnClickListener {
                val intent = Intent(context, GameDetailActivity::class.java)
                // O ID é crucial para a tela de detalhes buscar o jogo correto
                intent.putExtra("GAME_ID", promotion.id)
                context.startActivity(intent)
            }
        }
    }

    /**
     * Função para atualizar a lista do adapter.
     */
    fun updateList(newList: List<ItadPromotion>) {
        promotions = newList
        notifyDataSetChanged()
    }

    /**
     * Helper para mapear o NOME da loja para um logo local.
     */
    private fun getStoreLogo(shopName: String?): Int {
        return when (shopName) {
            "Steam" -> R.drawable.steam_logo
            "GOG" -> R.drawable.gog_logo
            "Ubisoft Store" -> R.drawable.ubisoft_store_logo
            "Epic Game Store" -> R.drawable.epic_games_logo
            "Origin" -> R.drawable.origin_logo
            "EA App" -> R.drawable.ea_app_logo
            "GreenManGaming" -> R.drawable.gmg_logo
            "Nuuvem" -> R.drawable.nuuvem_logo
            "Microsoft Store" -> R.drawable.microsoft_logo
            else -> R.drawable.ic_store_placeholder // Um ícone genérico
        }
    }
}