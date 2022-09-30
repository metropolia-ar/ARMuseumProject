package com.marsu.armuseumproject.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.navigation.navArgs
import com.marsu.armuseumproject.ArActivityArgs
import com.marsu.armuseumproject.R
import com.marsu.armuseumproject.database.Artwork
import com.marsu.armuseumproject.databinding.FragmentApiServiceBinding
import com.marsu.armuseumproject.databinding.FragmentArtInfoBinding
import com.squareup.picasso.Picasso


class ArtInfoFragment : Fragment() {

    private lateinit var binding: FragmentArtInfoBinding
//    private lateinit var art: Artwork
    private val args: ArtInfoFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentArtInfoBinding.inflate(inflater)
        binding.art = args.art

        // TODO: Navigate to this fragment without resetting the recyclerview in ApiServiceFragment.
        //  Maybe instantiate list onStart or onCreate etc?


        try {
            Picasso.get()
                .load(binding.art?.primaryImage)
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