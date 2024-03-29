package com.vinag.animack.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vinag.animack.databinding.RecommendationItemBinding
import com.vinag.animack.models.recommendation.Data
import com.vinag.animack.models.recommendation.Entry

class RecommendationsAdapter : RecyclerView.Adapter<RecommendationsAdapter.AnimeViewHolder>() {

    var onItemClick : ((Entry) -> Unit)? = null

    inner class AnimeViewHolder(val binding : RecommendationItemBinding) : RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<Data>(){
        override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem.mal_id == newItem.mal_id
        }

        override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem == newItem
        }

    }

    var differ = AsyncListDiffer(this, differCallback)


    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {
        val data = differ.currentList[position]


        //retrieve only the first data entry since the other data is duplicate only
        val recommendation = data.entry[0]

        val imageURL = recommendation.images.jpg.image_url
        val title = recommendation.title

        holder.binding.apply {
            Glide
                .with(holder.itemView)
                .load(imageURL)
                .into(ivRecommendedSrc)
            tvRecommendedTitle.text = title
        }

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(recommendation)
        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeViewHolder {
        return AnimeViewHolder(
            RecommendationItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


}