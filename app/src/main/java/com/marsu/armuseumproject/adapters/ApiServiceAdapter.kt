package com.marsu.armuseumproject.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.marsu.armuseumproject.R
import com.marsu.armuseumproject.database.Artwork
import com.marsu.armuseumproject.databinding.ArtListItemBinding
import com.squareup.picasso.Picasso
import javax.inject.Inject

class ApiServiceAdapter: RecyclerView.Adapter<ApiServiceAdapter.ApiServiceViewHolder>() {

    var onItemClick: ((Artwork) -> Unit)? = null
    private var artList: List<Artwork> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApiServiceViewHolder {
        val binding = ArtListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ApiServiceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ApiServiceViewHolder, position: Int) {


        val art = artList[position]
        holder.binding.art = art

        var artistName = ""
        if (art.artistDisplayName.isNotEmpty()) artistName = "By ${art.artistDisplayName}"
        holder.binding.imageArtist.text = artistName

        try {
            Picasso.get()
                .load(artList[position].primaryImageSmall)
                .resize(150, 150)
                .centerCrop()
                .error(R.drawable.ic_not_found_vector)
                .into(holder.binding.artThumbnail)
            Log.d("onBindViewHolder", "image loaded succesfully")
        } catch (e: Exception) {
            Log.d("Exception when loading image", e.message.toString())
        }

    }

    override fun getItemCount() = artList.size

    override fun getItemViewType(position: Int) = position

    /** Updates the data in the memberList variable. */
    fun setData(arts: List<Artwork>) {
        this.artList = arts
        notifyDataSetChanged()
    }

    // Add a onItemClick function that can be called from fragments via ApiServiceAdapter.onItemClick = {}
    inner class ApiServiceViewHolder(val binding: ArtListItemBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.recyclerCard.setOnClickListener {
                onItemClick?.invoke(artList[adapterPosition])
            }
        }
    }
}

