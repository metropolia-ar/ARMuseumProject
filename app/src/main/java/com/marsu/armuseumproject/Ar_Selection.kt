package com.marsu.armuseumproject

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.findNavController
import com.marsu.armuseumproject.databinding.FragmentArSelectionBinding

const val SELECT_PICTURE = 200

class Ar_Selection : Fragment() {

    private var _binding: FragmentArSelectionBinding? = null;
    private val binding get() = _binding!!

    private lateinit var previewImage: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentArSelectionBinding.inflate(inflater, container, false)
        val view = binding.root

        previewImage = binding.previewPhoto

        binding.backButton.setOnClickListener { view.findNavController().navigate(R.id.action_ar_Selection_to_homeFragment) }
        binding.selectPhotoButton.setOnClickListener {
            val i = Intent()
            i.type = "image/*"
            i.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(i, "Select photo"), SELECT_PICTURE)
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            val selectedImageUri = data?.data
            if (null != selectedImageUri) {
                previewImage.setImageURI(selectedImageUri)
            }
        }
    }

}