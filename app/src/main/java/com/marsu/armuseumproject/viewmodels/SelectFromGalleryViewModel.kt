package com.marsu.armuseumproject


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marsu.armuseumproject.database.ArtDB
import com.marsu.armuseumproject.database.Artwork
import kotlinx.coroutines.launch

class SelectFromGalleryViewModel : ViewModel() {


    private val database = ArtDB.get(MyApp.appContext)
    fun insertImage(artwork: Artwork) {
        viewModelScope.launch { database.artDao().addArtwork(artwork) }

    }
}