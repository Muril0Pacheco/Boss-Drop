package com.example.bossdrop.adapter

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bossdrop.R
import com.example.bossdrop.data.model.ItadPromotion // ◀️ --- IMPORT MUDOU
import com.example.bossdrop.databinding.ListItemPromotionBinding
import com.example.bossdrop.ui.detail.GameDetailActivity // ◀️ --- IMPORT NOVO
import java.text.NumberFormat
import java.util.Locale

class PromotionAdapter(
    // ◀️ --- TIPO ALTERADO ---
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

        // Formata os preços para BRL (ex: 55.0 -> "R$ 55,00")
        val brlFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

        holder.binding.apply {
            // Pega os dados dos sub-objetos (com segurança)
            val title = promotion.title
            val deal = promotion.deal
            val assets = promotion.assets
            val shopName = deal?.shop?.name

            // Preenche a UI
            gameTitleTextView.text = title
            discountTextView.text = "-${deal?.cut}%"
            newPriceTextView.text = brlFormat.format(deal?.price?.amount ?: 0.0)
            oldPriceTextView.text = brlFormat.format(deal?.regular?.amount ?: 0.0)

            // Adiciona o efeito de texto riscado
            oldPriceTextView.paintFlags = oldPriceTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

            // Usa o Glide para carregar a imagem da capa (boxart)
            Glide.with(context)
                .load(assets?.boxart)
                .placeholder(R.drawable.ic_image_placeholder) // Placeholder
                .into(gameImageView)

            // Usa a função helper para definir o logo da loja
            storeLogoImageView.setImageResource(getStoreLogo(shopName))

            // Adiciona o clique para abrir a Tela de Detalhes
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
    fun updateList(newList: List<ItadPromotion>) { // ◀️ --- TIPO ALTERADO ---
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
            "Fanatical" -> R.drawable.fanatical_logo
            "Humble Store" -> R.drawable.humble_store_logo
            "Green Man Gaming" -> R.drawable.gmg_logo
            "Nuuvem" -> R.drawable.nuuvem_logo
            // Adicione outros logos que você tiver
            else -> R.drawable.ic_store_placeholder // Um ícone genérico
        }
    }
}