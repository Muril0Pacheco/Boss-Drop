package com.example.bossdrop.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bossdrop.R
import com.example.bossdrop.data.model.ItadPromotion
import com.example.bossdrop.ui.detail.GameDetailActivity
import com.example.bossdrop.databinding.GridItemRecommendedBinding
// ◀️ --- Import do ListItemSearchQueryBinding REMOVIDO ---
import java.text.NumberFormat
import java.util.Locale

// ◀️ --- Classe SearchHistoryAdapter REMOVIDA ---
// (Não precisamos mais dela)

class RecommendedAdapter : RecyclerView.Adapter<RecommendedAdapter.ViewHolder>() {

    private var promotions: List<ItadPromotion> = emptyList()

    // O ViewHolder não muda, ele continua pegando o binding do item
    inner class ViewHolder(val binding: GridItemRecommendedBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = GridItemRecommendedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // ◀️ --- onBindViewHolder ATUALIZADO ---
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val promotion = promotions[position]
        val context = holder.itemView.context

        // 1. (NOVO) Define o título do jogo
        //    (Assumindo que o ID no XML é 'itemGameTitle')
        holder.binding.itemGameTitle.text = promotion.title

        // 2. Carrega a imagem da capa (boxart)
        //    (Assumindo que o ID no XML é 'itemGameImage')
        Glide.with(context)
            .load(promotion.assets?.boxart)
            .placeholder(R.drawable.ic_store_placeholder) // Imagem de loading
            .error(R.drawable.ic_store_placeholder)       // Imagem em caso de erro
            .into(holder.binding.itemGameImage) // ◀️ --- ID da imagem do XML

        // O clique continua o mesmo, levando para a tela de Detalhes
        holder.itemView.setOnClickListener {
            val clickedPromotion = promotions[position]
            val intent = Intent(context, GameDetailActivity::class.java)
            intent.putExtra("GAME_ID", clickedPromotion.id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = promotions.size

    // A função de update continua a mesma
    fun updateList(newList: List<ItadPromotion>) {
        promotions = newList
        notifyDataSetChanged()
    }
}