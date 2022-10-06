package com.marsu.armuseumproject.fragments

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.net.toUri
import androidx.navigation.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.marsu.armuseumproject.MyApp
import com.marsu.armuseumproject.R
import com.marsu.armuseumproject.SelectFromGalleryViewModel
import com.marsu.armuseumproject.database.Artwork
import com.marsu.armuseumproject.databinding.FragmentSelectFromGalleryBinding
import org.apache.commons.io.IOUtils
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*

const val REQUEST_CODE = 200


class SelectFromGallery : Fragment() {
    private var entryId: Int = 0
    private var resultUri: Uri? = null
    private var imageUri: Uri? = null
    private var _binding: FragmentSelectFromGalleryBinding? = null
    private val binding get() = _binding!!


    companion object {
        private lateinit var viewModel: SelectFromGalleryViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        entryId = UUID.randomUUID().hashCode() * -1
        _binding = FragmentSelectFromGalleryBinding.inflate(inflater, container, false)
        viewModel = SelectFromGalleryViewModel()

        val view = binding.root
        val button: Button = binding.ChooseImage
        val saveButton: Button = binding.saveButton
        val titleEditText = binding.inputTitle
        val artistEditText = binding.inputArtist
        val departmentEditText = binding.inputDepartment
        val classEditText = binding.inputClassification
        val imgView = binding.imageFromGallery
        val constraint = binding.ConstraintLayout

        imgView.setImageResource(R.drawable.ic_baseline_image_24)

        constraint.setOnClickListener {
            clearFocuses(
                titleEditText,
                artistEditText,
                departmentEditText,
                classEditText,
                constraint
            )
        }

        imgView.setOnClickListener {
            openGalleryForImage()
        }
        button.setOnClickListener {
            openGalleryForImage()
        }
        saveButton.setOnClickListener {
            if (resultUri == null || titleEditText.text.toString() == "") {
                Toast.makeText(
                    MyApp.appContext,
                    getString(R.string.pickImageToast),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                saveFileToInternalStorage()
                insertToDatabase(
                    viewModel,
                    imageUri,
                    titleEditText.text.toString(),
                    artistEditText.text.toString(),
                    departmentEditText.text.toString(),
                    classEditText.text.toString()
                )
                clearEditTexts(
                    titleEditText,
                    artistEditText,
                    departmentEditText,
                    classEditText,
                    imgView
                )
            }
        }

        return view
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE)
    }

    // Save photos into internal storage to have access to them always
    // Null check done before function call, therefore non-null asserted calls (!!)
    private fun saveFileToInternalStorage() {
        val contentResolver = requireContext().contentResolver
        val newFile = File(requireContext().filesDir.absolutePath, "$entryId")

        var inputStream: InputStream? = null
        val byteStream = ByteArrayOutputStream()
        var fileOutputStream: FileOutputStream? = null
        try {
            inputStream = contentResolver.openInputStream(resultUri!!)
            fileOutputStream = FileOutputStream(newFile)

            IOUtils.copy(inputStream, byteStream)
            val bytes = byteStream.toByteArray()

            fileOutputStream.write(bytes)

            imageUri = newFile.toUri()
        } catch (e: Exception) {
            Log.e("IMG_CREATE", "Failed to copy image from gallery", e)

            inputStream?.close()
            fileOutputStream?.close()
            byteStream.close()

            return
        } finally {
            inputStream?.close()
            fileOutputStream?.close()
            byteStream.close()
        }
    }

    private fun insertToDatabase(
        viewModel: SelectFromGalleryViewModel,
        uri: Uri?,
        title: String,
        artist: String,
        department: String,
        classification: String
    ) {
        viewModel.insertImage(
            Artwork(
                entryId,
                uri.toString(),
                uri.toString(),
                department,
                title,
                artist,
                classification
            )
        )
    }

    private fun clearEditTexts(
        title: TextInputEditText,
        artist: TextInputEditText,
        department: TextInputEditText,
        classification: TextInputEditText,
        imgView: ImageView
    ) {
        title.setText("")
        artist.setText("")
        department.setText("")
        classification.setText("")
        title.clearFocus()
        artist.clearFocus()
        department.clearFocus()
        classification.clearFocus()
        imgView.setImageResource(R.drawable.ic_baseline_image_24)
        resultUri = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val imgView: ImageView = binding.imageFromGallery
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == REQUEST_CODE) {
            resultUri = data?.data
            imgView.setImageURI(resultUri)
        }
    }

    private fun closeKeyBoard(view: View) {
        val imm: InputMethodManager =
            view.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun clearFocuses(
        title: TextInputEditText,
        artist: TextInputEditText,
        department: TextInputEditText,
        classification: TextInputEditText,
        view: View
    ) {
        title.clearFocus()
        artist.clearFocus()
        department.clearFocus()
        classification.clearFocus()
        closeKeyBoard(view)
    }
}