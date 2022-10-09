package com.marsu.armuseumproject.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.marsu.armuseumproject.R
import com.marsu.armuseumproject.databinding.FragmentArtInfoBinding
import com.marsu.armuseumproject.service.InternalStorageService
import com.marsu.armuseumproject.viewmodels.ArtInfoViewModel
import com.squareup.picasso.Picasso


/**
 * View single Artwork objects found from the API. Contains functionality to save the image to the internal storage and Room DB.
 */
class ArtInfoFragment : Fragment() {

    private lateinit var binding: FragmentArtInfoBinding
    private lateinit var viewModel: ArtInfoViewModel

    private val args: ArtInfoFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel = ArtInfoViewModel(args.art)

        binding = FragmentArtInfoBinding.inflate(inflater)
        binding.artInfoViewModel = viewModel
        binding.lifecycleOwner = this

        binding.artInfoSaveImage.setOnClickListener { viewModel.insertImage() }

        viewModel.saveSuccess.observe(viewLifecycleOwner) {
            it.let {
                binding.artInfoSaveImage.isEnabled = !it
            }
        }

        try {
            Picasso.get()
                .load(viewModel.art.primaryImageSmall)
                .fit()
                .centerCrop()
                .error(R.drawable.ic_not_found_vector)
                .into(binding.artImageLarge)
            Log.d("onBindViewHolder", "image loaded succesfully")
        } catch (e: Exception) {
            Log.d("Exception when loading image", e.message.toString())
        }

        return binding.root
    }

}