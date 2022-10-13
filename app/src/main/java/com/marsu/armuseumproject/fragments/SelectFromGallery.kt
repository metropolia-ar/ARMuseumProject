package com.marsu.armuseumproject.fragments

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.marsu.armuseumproject.MyApp
import com.marsu.armuseumproject.R
import com.marsu.armuseumproject.SelectFromGalleryViewModel
import com.marsu.armuseumproject.database.Artwork
import com.marsu.armuseumproject.databinding.FragmentSelectFromGalleryBinding
import com.marsu.armuseumproject.service.InternalStorageService
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
        val saveButton: Button = binding.saveButton
        val titleEditText = binding.inputTitle
        val artistEditText = binding.inputArtist
        val imgView = binding.imageFromGallery
        val constraint = binding.ConstraintLayout

        imgView.setImageResource(R.drawable.ic_baseline_add_a_photo_24)

        constraint.setOnClickListener {
            clearFocuses(
                titleEditText,
                artistEditText,
                constraint
            )
        }

        imgView.setOnClickListener {
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
                val newUri = InternalStorageService.saveFileToInternalStorage(resultUri)
                insertToDatabase(
                    viewModel,
                    newUri,
                    titleEditText.text.toString(),
                    artistEditText.text.toString()
                )
                clearEditTexts(
                    titleEditText,
                    artistEditText,
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


    private fun insertToDatabase(
        viewModel: SelectFromGalleryViewModel,
        uri: Uri?,
        title: String,
        artist: String,
    ) {
        viewModel.insertImage(
            Artwork(
                entryId,
                uri.toString(),
                uri.toString(),
                "",
                title,
                artist,
                ""
            )
        )
    }

    private fun clearEditTexts(
        title: TextInputEditText,
        artist: TextInputEditText,
        imgView: ImageView
    ) {
        title.setText("")
        artist.setText("")
        title.clearFocus()
        artist.clearFocus()
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
        view: View
    ) {
        title.clearFocus()
        artist.clearFocus()
        closeKeyBoard(view)
    }
}