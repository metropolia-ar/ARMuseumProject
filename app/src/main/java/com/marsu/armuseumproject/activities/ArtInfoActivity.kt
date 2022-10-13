package com.marsu.armuseumproject.activities

import android.os.Bundle
import android.util.Log
import androidx.navigation.navArgs
import com.marsu.armuseumproject.R
import com.marsu.armuseumproject.databinding.ActivityArtInfoBinding
import com.marsu.armuseumproject.viewmodels.ArtInfoViewModel
import com.squareup.picasso.Picasso

/**
 * Displays a popup window with basic information of the Artwork and contains functionality for saving the Artwork to Room DB.
 */
class ArtInfoActivity : PopupActivity() {

    private lateinit var binding: ActivityArtInfoBinding
    private lateinit var viewModel: ArtInfoViewModel
    private val args: ArtInfoActivityArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ArtInfoViewModel(args.art)
        binding = ActivityArtInfoBinding.inflate(layoutInflater)

        setContentView(binding.root)
        binding.artInfoViewModel = viewModel
        binding.lifecycleOwner = this

        binding.artInfoSaveImage.setOnClickListener { viewModel.insertImage() }
        binding.artInfoBack.setOnClickListener { finish() }

        viewModel.saveSuccess.observe(this) {
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
    }

}