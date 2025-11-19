package com.example.bossdrop.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.bossdrop.R
import com.example.bossdrop.data.model.ItadPromotion
import com.example.bossdrop.ui.detail.GameDetailActivity
import com.example.bossdrop.databinding.GridItemRecommendedBinding

class RecommendedAdapter : RecyclerView.Adapter<RecommendedAdapter.ViewHolder>() {

    private var promotions: List<ItadPromotion> = emptyList()

    inner class ViewHolder(val binding: GridItemRecommendedBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = GridItemRecommendedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val promotion = promotions[position]
        val context = holder.itemView.context


        holder.binding.itemGameTitle.text = promotion.title


        Glide.with(context)
            .load(promotion.assets?.boxart)
            .placeholder(R.drawable.ic_store_placeholder)
            .error(R.drawable.ic_store_placeholder)
            .centerCrop()
            .override(400, 400)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.binding.itemGameImage)

        holder.itemView.setOnClickListener {
            val clickedPromotion = promotions[position]
            val intent = Intent(context, GameDetailActivity::class.java)
            intent.putExtra("GAME_ID", clickedPromotion.id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = promotions.size

    fun updateList(newList: List<ItadPromotion>) {
        promotions = newList
        notifyDataSetChanged()
    }
}