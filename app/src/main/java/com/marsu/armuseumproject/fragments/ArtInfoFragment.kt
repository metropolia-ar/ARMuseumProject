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
import com.squareup.picasso.Picasso


class ArtInfoFragment : Fragment() {

    private lateinit var binding: FragmentArtInfoBinding
    private val args: ArtInfoFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentArtInfoBinding.inflate(inflater)
        binding.art = args.art

        try {
            Picasso.get()
                .load(binding.art?.primaryImageSmall)
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