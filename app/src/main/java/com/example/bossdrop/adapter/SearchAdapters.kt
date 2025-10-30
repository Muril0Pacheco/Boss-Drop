package com.example.bossdrop.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bossdrop.databinding.GridItemRecommendedBinding
import com.example.bossdrop.databinding.ListItemSearchQueryBinding

// Adapter para a lista de "Mais Buscados"
class SearchHistoryAdapter(private val queries: List<String>) :
    RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ListItemSearchQueryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemSearchQueryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.queryTextView.text = queries[position]
    }

    override fun getItemCount() = queries.size
}

// Adapter para a grade de "Recomendados"
class RecommendedAdapter(private val itemCount: Int) :
    RecyclerView.Adapter<RecommendedAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: GridItemRecommendedBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = GridItemRecommendedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // Não precisamos de dados reais, apenas placeholders
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {}

    override fun getItemCount() = itemCount
}