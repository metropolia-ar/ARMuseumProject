package com.marsu.armuseumproject.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gorisse.thomas.sceneform.util.toByteArray
import com.marsu.armuseumproject.MyApp
import com.marsu.armuseumproject.database.ArtDB
import com.marsu.armuseumproject.database.Artwork
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.apache.commons.io.IOUtils
import retrofit2.http.Url
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

/**
 * Provides service for storing images to the internal storage and Room DB.
 */
class InternalStorageService(val context: Context) {

    private val dir = context.filesDir.absolutePath
    private val database = ArtDB.get(MyApp.appContext)

    /**
     * Adds the given Artwork object to the Room DB.
     */
    private suspend fun insertImage(artwork: Artwork) = database.artDao().addArtwork(artwork)

    /**
     * Copies an image from the gallery to the internal storage.
     */
    fun saveFileToInternalStorage(location: Uri) {

        val entryId = UUID.randomUUID().hashCode() * -1
        val contentResolver = context.contentResolver
        val newFile = File(dir, "$entryId")

        var inputStream: InputStream? = null
        val byteStream = ByteArrayOutputStream()
        var fileOutputStream: FileOutputStream? = null

        try {

            inputStream = contentResolver.openInputStream(location)
            fileOutputStream = FileOutputStream(newFile)

            IOUtils.copy(inputStream, byteStream)
            val bytes = byteStream.toByteArray()

            fileOutputStream.write(bytes)

            val imageUri = newFile.toUri()
        } catch (e: Exception) {
            Log.e("IMG_CREATE", "Failed to copy image", e)

            inputStream?.close()
            fileOutputStream?.close()
            byteStream.close()

            return
        } finally {
            inputStream?.close()
            fileOutputStream?.close()
            byteStream.close()
            Log.d("Saved image to Internal storage", "success")
        }
    }

    /**
     * Copies an image from a URL, and saves it to the internal storage.
     * @return the location of the image in the internal storage.
     */
    private fun saveFileToInternalStorage(url: URL): String {

        var id : Int? = UUID.randomUUID().hashCode() * -1
        var newDir = "$dir/$id.png"

        var inputStream: InputStream? = null
        var fileOutputStream: FileOutputStream? = null

        try {

            fileOutputStream = FileOutputStream(newDir)
            val con = url.openConnection() as HttpURLConnection
            con.doInput = true
            con.connect()

            if (con.responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = con.inputStream
                val bmp = inputStream.readBytes()
                fileOutputStream.write(bmp)
            }
        }  catch (e: Exception) {
            Log.e("IMG_CREATE", "Failed to copy image", e)
            inputStream?.close()
            fileOutputStream?.close()
            newDir = ""
        } finally {
            inputStream?.close()
            fileOutputStream?.close()
            Log.d("Saved image to Internal storage", "success")
        }

        return newDir
    }


    /**
     * Saves the selected API Artwork object to Room DB and the image to the internal storage.
     * @return was the operation successful.
     */
    suspend fun saveApiArtWorkToRoom(art: Artwork): Boolean {

        val asd = CoroutineScope(Dispatchers.IO).async {
            try {
                val newDir = saveFileToInternalStorage(URL(art.primaryImageSmall))
                val newArt = art.copy()
                newArt.primaryImageSmall = newDir
                newArt.primaryImage = newDir
                insertImage(newArt)
                Log.d("Artwork saved, new dir", newDir)
                true
            } catch (e: Exception) {
                Log.e("IMG_CREATE", "Failed to save api img to Room", e)
                false
            }
        }
        return asd.await()
    }

}