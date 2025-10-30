package com.example.bossdrop.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bossdrop.databinding.ListItemFavoriteBinding
import com.example.bossdrop.data.model.FavoriteItem

class FavoritesAdapter(private var favoriteItems: List<FavoriteItem>) :
    RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder>() {

    inner class FavoriteViewHolder(val binding: ListItemFavoriteBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ListItemFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun getItemCount() = favoriteItems.size

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val favorite = favoriteItems[position]
        holder.binding.gameImageView.setImageResource(favorite.gameImageResId)
        holder.binding.gameTitleTextView.text = favorite.gameTitle
        holder.binding.gamePriceTextView.text = favorite.gamePrice
    }

    fun updateList(newList: List<FavoriteItem>) {
        favoriteItems = newList
        notifyDataSetChanged()
    }
}
