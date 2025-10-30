package com.example.bossdrop.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bossdrop.databinding.ListItemPromotionBinding
import com.example.bossdrop.data.model.Promotion

class PromotionAdapter(private val promotions: List<Promotion>) :
    RecyclerView.Adapter<PromotionAdapter.PromotionViewHolder>() {

    inner class PromotionViewHolder(val binding: ListItemPromotionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromotionViewHolder {
        val binding = ListItemPromotionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PromotionViewHolder(binding)
    }

    override fun getItemCount() = promotions.size

    override fun onBindViewHolder(holder: PromotionViewHolder, position: Int) {
        val promotion = promotions[position]
        holder.binding.apply {
            gameImageView.setImageResource(promotion.gameImageResId)
            gameTitleTextView.text = promotion.gameTitle
            discountTextView.text = promotion.discount
            oldPriceTextView.text = promotion.oldPrice
            // Adiciona o efeito de texto riscado
            oldPriceTextView.paintFlags = oldPriceTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            newPriceTextView.text = promotion.newPrice
            storeLogoImageView.setImageResource(promotion.storeLogoResId)
        }
    }
}