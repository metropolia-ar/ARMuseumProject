package com.marsu.armuseumproject

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.marsu.armuseumproject.databinding.FragmentArSelectionBinding
import com.marsu.armuseumproject.viewmodels.ArSelectionViewModel

const val SELECT_PICTURE = 200

class ArSelection : Fragment() {

    companion object {
        lateinit var arSelectionViewModel: ArSelectionViewModel
    }
    private var _binding: FragmentArSelectionBinding? = null;
    private val binding get() = _binding!!

    private lateinit var previewImage: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arSelectionViewModel = ArSelectionViewModel(requireActivity().application)
        arSelectionViewModel.imageUri.observe(viewLifecycleOwner) {
            Log.d("URI", it.toString())
            enableStartButton(it)
        }

        _binding = FragmentArSelectionBinding.inflate(inflater, container, false)
        val view = binding.root

        previewImage = binding.previewPhoto

        binding.backButton.setOnClickListener { view.findNavController().navigate(R.id.action_ar_Selection_to_homeFragment) }
        binding.startArButton.setOnClickListener { navigateToArActivity(view) }

        binding.selectPhotoButton.setOnClickListener {
            val i = Intent()
            i.type = "image/*"
            i.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(i, "Select photo"), SELECT_PICTURE)
        }

        return view
    }


    // When image has been selected, save its URI to the view model
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            val selectedImageUri = data?.data
            if (null != selectedImageUri) {
                previewImage.setImageURI(selectedImageUri)
                arSelectionViewModel.imageUri.postValue(selectedImageUri)
            }
        }
    }

    // Enable 'Start AR' button once an image has been selected
    private fun enableStartButton(value: Uri?) {
        if (value != null) {
            binding.startArButton.isEnabled = true
        }
    }

    private fun navigateToArActivity(v: View) {
        val uri = arSelectionViewModel.imageUri.value.toString()
        val action = ArSelectionDirections.actionArSelectionToArActivity(uri)
        v.findNavController().navigate(action)
    }

}