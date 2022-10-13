package com.marsu.armuseumproject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marsu.armuseumproject.database.Artwork
import com.marsu.armuseumproject.service.InternalStorageService
import kotlinx.coroutines.launch

open class SelectFromGalleryViewModel : ViewModel() {

    fun insertImage(artwork: Artwork) {
        viewModelScope.launch { InternalStorageService.insertImage(artwork) }
    }
}