package com.example.bossdrop.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // Importar o Glide
import com.example.bossdrop.R // Importar R
import com.example.bossdrop.data.model.FavoriteItem
import com.example.bossdrop.databinding.ListItemFavoriteBinding

class FavoritesAdapter(
    private var favoriteItems: List<FavoriteItem>,
    private val onItemClick: (String) -> Unit // Novo: Click listener
) :
    RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder>() {

    inner class FavoriteViewHolder(val binding: ListItemFavoriteBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding =
            ListItemFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun getItemCount() = favoriteItems.size

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val favorite = favoriteItems[position]

        holder.binding.gameTitleTextView.text = favorite.gameTitle
        holder.binding.gamePriceTextView.text = favorite.gamePrice

        // Modificado: Usar Glide para carregar a imagem pela URL
        Glide.with(holder.itemView.context)
            .load(favorite.gameImageUrl)
            .placeholder(R.drawable.ic_store_placeholder) // Um placeholder
            .into(holder.binding.gameImageView)

        // Novo: Lógica para mostrar o desconto (se existir)
        // (Assumindo que você tem um TextView com id 'gameDiscountTextView' no seu layout)
        if (favorite.gameDiscount.isNullOrEmpty()) {
            holder.binding.gameDiscountTextView.visibility = View.GONE
        } else {
            holder.binding.gameDiscountTextView.visibility = View.VISIBLE
            holder.binding.gameDiscountTextView.text = favorite.gameDiscount
        }

        // Novo: Definir o clique no item
        holder.itemView.setOnClickListener {
            onItemClick(favorite.gameId)
        }
    }

    fun updateList(newList: List<FavoriteItem>) {
        favoriteItems = newList
        notifyDataSetChanged()
    }
}