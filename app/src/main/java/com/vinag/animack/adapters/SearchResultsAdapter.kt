package com.vinag.animack.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vinag.animack.databinding.SearchResultsItemBinding
import com.vinag.animack.models.searchAnime.Data

class SearchResultsAdapter : RecyclerView.Adapter<SearchResultsAdapter.ResultViewHolder>() {

    var onItemClick : ((Data) -> Unit)? = null

    inner class ResultViewHolder(val binding : SearchResultsItemBinding) : RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<Data>(){
        override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem.mal_id == newItem.mal_id
        }

        override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem == newItem
        }
    }

    var differ = AsyncListDiffer(this, differCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        return ResultViewHolder(
            SearchResultsItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val result = differ.currentList[position]

        holder.binding.apply {
            tvSearchTitle.text  = result.title
            Glide
                .with(holder.itemView)
                .load(result.images.jpg.image_url)
                .into(ivSearchSrc)
        }

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(result)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}