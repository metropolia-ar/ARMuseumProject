package com.marsu.armuseumproject.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.marsu.armuseumproject.database.Artwork
import com.marsu.armuseumproject.databinding.ArtListItemBinding

class ArSelectionAdapter : RecyclerView.Adapter<ArSelectionAdapter.ArSelectionViewHolder>() {

    var onItemClick: ((Artwork) -> Unit)? = null
    private var artList: List<Artwork> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArSelectionViewHolder {
        val binding = ArtListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArSelectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArSelectionViewHolder, position: Int) {

        val art = artList[position]
        holder.binding.art = art

        var artistName = ""
        if (art.artistDisplayName.isNotEmpty()) artistName = "By ${art.artistDisplayName}"
        holder.binding.imageArtist.text = artistName
        holder.binding.artThumbnail.setImageURI(art.primaryImageSmall.toUri())

    }

    override fun getItemCount() = artList.size

    override fun getItemViewType(position: Int) = position

    /** Updates the data in the memberList variable. */
    fun setData(arts: List<Artwork>) {
        this.artList = arts
        notifyDataSetChanged()
    }

    // Add a onItemClick function that can be called from fragments via ApiServiceAdapter.onItemClick = {}
    inner class ArSelectionViewHolder(val binding: ArtListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.recyclerCard.setOnClickListener {
                onItemClick?.invoke(artList[adapterPosition])
            }
        }
    }
}

