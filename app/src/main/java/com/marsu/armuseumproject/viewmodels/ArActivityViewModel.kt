package com.marsu.armuseumproject.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.ux.TransformableNode

class ArActivityViewModel(application: Application) : AndroidViewModel(application) {
    val currentImageNode = MutableLiveData<Node>(null)
}