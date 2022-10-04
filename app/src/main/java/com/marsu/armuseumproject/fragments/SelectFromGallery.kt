package com.marsu.armuseumproject.fragments

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.findNavController
import com.google.android.material.textfield.TextInputEditText
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
        val backButton: LinearLayout = binding.sfgBackButton
        val titleEditText = binding.inputTitle
        val artistEditText = binding.inputArtist
        val departmentEditText = binding.inputDepartment
        val classEditText = binding.inputClassification
        val imgView = binding.imageFromGallery
        val constraint = binding.ConstraintLayout

        imgView.setImageResource(R.drawable.image_not_found)

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
            if (imageUri == null || titleEditText.text.toString() == "") {
                Toast.makeText(
                    MyApp.appContext,
                    getString(R.string.pickImageToast),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
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

        backButton.setOnClickListener {
            view.findNavController().navigate(R.id.action_selectFromGallery_to_homeFragment)
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
        department: String,
        classification: String
    ) {
        val objectID: Int = uri.hashCode() * -1
        Log.d("HASHCODE TEST", objectID.toString())

        viewModel.insertImage(
            Artwork(
                objectID,
                uri.toString(),
                "",
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
        imgView.setImageResource(R.drawable.image_not_found)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val imgView: ImageView = binding.imageFromGallery
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == REQUEST_CODE) {
            imageUri = data?.data
            imgView.setImageURI(imageUri)
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