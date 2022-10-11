package com.marsu.armuseumproject.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.marsu.armuseumproject.database.Artwork
import com.marsu.armuseumproject.databinding.ArtListItemBinding

class HomeRecyclerAdapter: RecyclerView.Adapter<HomeRecyclerAdapter.HomeRecyclerViewHolder>() {

    var onItemClick: ((Artwork) -> Unit)? = null
    private var lastFiveObject: List<Artwork> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeRecyclerViewHolder {
        val binding = ArtListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeRecyclerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeRecyclerViewHolder, position: Int) {
        val art = lastFiveObject[position]
        holder.binding.art = art

        var artistName = ""
        if (art.artistDisplayName.isNotEmpty()) artistName = "By ${art.artistDisplayName}"
        holder.binding.imageArtist.text = artistName
        holder.binding.artThumbnail.setImageURI(art.primaryImage.toUri())
    }

    override fun getItemCount() = lastFiveObject.size
    override fun getItemViewType(position: Int) = position

    fun setData(arts: List<Artwork>) {
        this.lastFiveObject = arts
        notifyDataSetChanged()
    }

    inner class HomeRecyclerViewHolder(val binding: ArtListItemBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.recyclerCard.setOnClickListener {
                onItemClick?.invoke(lastFiveObject[adapterPosition])
            }
        }
    }
}