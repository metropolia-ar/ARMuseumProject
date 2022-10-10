package com.marsu.armuseumproject.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marsu.armuseumproject.MyApp
import com.marsu.armuseumproject.R
import com.marsu.armuseumproject.SelectFromGalleryViewModel
import com.marsu.armuseumproject.database.ArtDB
import com.marsu.armuseumproject.database.Artwork
import com.marsu.armuseumproject.service.InternalStorageService
import kotlinx.coroutines.launch
import org.apache.commons.io.IOUtils
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URI
import java.net.URL


/**
 * ViewModel for ArtInfoFragment. Provides functionality for adding API images to Room and internal storage.
 */
class ArtInfoViewModel(val art: Artwork): ViewModel() {

    val saveSuccess = MutableLiveData(false)
    val savedText = MutableLiveData(MyApp.appContext.getString(R.string.save_image))

    /**
     * Saves the image to the internal storage and Room DB and set indicators for the UI.
     */
    fun insertImage() {
        viewModelScope.launch {
            saveSuccess.value = InternalStorageService.saveApiArtWorkToRoom(art)
            if (saveSuccess.value == true) {
                savedText.value = MyApp.appContext.getString(R.string.saved)
                Toast.makeText(MyApp.appContext, "${MyApp.appContext.getString(R.string.saved)}.", Toast.LENGTH_SHORT).show()
            }
            else savedText.value = MyApp.appContext.getString(R.string.save_image)
        }
    }
}