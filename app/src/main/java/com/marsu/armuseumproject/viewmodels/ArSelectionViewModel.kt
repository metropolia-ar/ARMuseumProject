package com.marsu.armuseumproject.viewmodels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class ArSelectionViewModel(application: Application): AndroidViewModel(application) {
    val imageUri = MutableLiveData<Uri?>(null);
}