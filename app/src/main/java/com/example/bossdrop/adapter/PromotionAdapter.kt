package com.example.bossdrop.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bossdrop.R
import com.example.bossdrop.databinding.ListItemPromotionBinding
import com.example.bossdrop.data.model.Deal // <-- IMPORT MUDOU

class PromotionAdapter(
    private var deals: List<Deal> // <-- TIPO MUDOU
) : RecyclerView.Adapter<PromotionAdapter.PromotionViewHolder>() {

    inner class PromotionViewHolder(val binding: ListItemPromotionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromotionViewHolder {
        val binding = ListItemPromotionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PromotionViewHolder(binding)
    }

    override fun getItemCount() = deals.size

    override fun onBindViewHolder(holder: PromotionViewHolder, position: Int) {
        val deal = deals[position] // <-- Agora é um objeto "Deal"
        val context = holder.itemView.context

        holder.binding.apply {
            // 1. Preenche os textos com os dados da API
            gameTitleTextView.text = deal.title
            newPriceTextView.text = "R$ ${deal.salePrice}"  // Adiciona o "R$"
            oldPriceTextView.text = "R$ ${deal.normalPrice}"

            // 2. Formata o desconto (ex: "85.042521" -> "85%")
            val discountPercent = deal.savings.substringBefore(".") + "%"
            discountTextView.text = discountPercent

            // 3. Adiciona o efeito de texto riscado
            oldPriceTextView.paintFlags = oldPriceTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

            // 4. Carrega a IMAGEM DA INTERNET usando o Glide
            Glide.with(context)
                .load(deal.thumb) // A URL da imagem
                .placeholder(R.drawable.ic_image_placeholder) // Um placeholder
                .into(gameImageView) // Onde a imagem será exibida

            // 5. Define o logo da loja (exemplo simples)
            val storeLogo = getStoreLogo(deal.storeID)
            storeLogoImageView.setImageResource(storeLogo)
        }
    }

    /**
     * Mapeia um ID de loja da API para um drawable local.
     */
    private fun getStoreLogo(storeID: String): Int {
        return when (storeID) {
            "1" -> R.drawable.steam_logo // Steam
            "7" -> R.drawable.gog_logo // GOG
            "8" -> R.drawable.origin_logo // Origin
            "11" -> R.drawable.humble_store_logo // Humble Store
            "15" -> R.drawable.fanatical_logo // Fanatical
            "25" -> R.drawable.epic_games_logo // Epic Games
            //Solução temporária, posteriormente as imagens virão via a API
            else -> R.drawable.ic_image_placeholder // Um ícone genérico
        }
    }

    /**
     * Função para atualizar a lista do adapter de forma eficiente.
     */
    fun updateList(newList: List<Deal>) {
        deals = newList
        notifyDataSetChanged() // Avisa o RecyclerView para redesenhar
    }
}