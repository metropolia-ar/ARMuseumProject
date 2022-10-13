package com.marsu.armuseumproject.viewmodels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.marsu.armuseumproject.database.ArtDB
import com.marsu.armuseumproject.database.Artwork
import kotlinx.coroutines.launch

class ArSelectionViewModel(application: Application): AndroidViewModel(application) {
    private val database = ArtDB.get(application.applicationContext)

    val imageUri = MutableLiveData<Uri?>(null)
    val imageId = MutableLiveData<Int?>(null)
    var getAllArtwork : LiveData<List<Artwork>> = database.artDao().getAllArt()
}