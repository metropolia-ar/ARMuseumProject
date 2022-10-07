package com.marsu.armuseumproject.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import com.gorisse.thomas.sceneform.util.toByteArray
import com.marsu.armuseumproject.MyApp
import com.marsu.armuseumproject.database.ArtDB
import com.marsu.armuseumproject.database.Artwork
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

class InternalStorageService(val context: Context) {

    private var entryId: Int = 0
    private val dir = context.filesDir.absolutePath

    protected val database = ArtDB.get(MyApp.appContext)

    suspend fun insertImage(artwork: Artwork) = database.artDao().addArtwork(artwork)

    fun saveFileToInternalStorage(location: Uri) {

        entryId = UUID.randomUUID().hashCode() * -1

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

    fun saveFileToInternalStorage(url: URL) {

        CoroutineScope(Dispatchers.IO).launch {

            var inputStream: InputStream? = null
            var fileOutputStream: FileOutputStream? = null

            try {

                fileOutputStream = FileOutputStream("$dir/testing.png")
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

            } finally {

                inputStream?.close()
                fileOutputStream?.close()
                Log.d("Saved image to Internal storage", "success")

            }
        }

    }

}