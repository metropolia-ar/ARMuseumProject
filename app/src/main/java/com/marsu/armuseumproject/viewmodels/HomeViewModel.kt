package com.marsu.armuseumproject.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.marsu.armuseumproject.database.ArtDB
import com.marsu.armuseumproject.database.Artwork

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val database = ArtDB.get(application.applicationContext)
    fun getArt(id: Int): LiveData<List<Artwork>> = database.artDao().getSpecificArt(id)
}