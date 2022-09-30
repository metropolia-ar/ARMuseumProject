package com.marsu.armuseumproject.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.marsu.armuseumproject.MyApp
import com.marsu.armuseumproject.R
import com.marsu.armuseumproject.SelectFromGalleryViewModel
import com.marsu.armuseumproject.database.Artwork
import com.marsu.armuseumproject.databinding.FragmentSelectFromGalleryBinding

const val REQUEST_CODE = 200

class SelectFromGallery : Fragment() {
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
        _binding = FragmentSelectFromGalleryBinding.inflate(inflater, container, false)
        viewModel = SelectFromGalleryViewModel()
        val view = binding.root

        val button: Button = binding.ChooseImage
        val saveButton: Button = binding.saveButton


        button.setOnClickListener {
            openGalleryForImage()
        }
        saveButton.setOnClickListener {
            if (imageUri == null) {
                Toast.makeText(
                    MyApp.appContext,
                    getString(R.string.pickImageToast),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                insertToDatabase(viewModel, imageUri)
            }

        }
        return view
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE)
    }

    private fun insertToDatabase(viewModel: SelectFromGalleryViewModel, uri: Uri?) {
        val objectID: Int = uri.hashCode() * -1
        Log.d("HASHCODE TEST", objectID.toString())

        viewModel.insertImage(Artwork(objectID, uri.toString(), "", "", "", "", "", "", "", "", "", ""))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val imgView: ImageView = binding.imageFromGallery
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == REQUEST_CODE) {
            imageUri = data?.data
            imgView.setImageURI(imageUri)
        }
    }
}