package com.marsu.armuseumproject.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.marsu.armuseumproject.MyApp
import com.marsu.armuseumproject.SelectFromGalleryViewModel
import com.marsu.armuseumproject.database.ArtDB
import com.marsu.armuseumproject.database.Artwork
import com.marsu.armuseumproject.service.InternalStorageService
import org.apache.commons.io.IOUtils
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URI
import java.net.URL

class ArtInfoViewModel(val art: Artwork, val context: Context): SelectFromGalleryViewModel() {

    val internalStorageService = InternalStorageService(context)

    fun insertImage() {
        super.insertImage(art)
        saveImageToInternalStorage()
    }

    private fun saveImageToInternalStorage() {
        internalStorageService.saveFileToInternalStorage(URL(art.primaryImageSmall))
    }
}