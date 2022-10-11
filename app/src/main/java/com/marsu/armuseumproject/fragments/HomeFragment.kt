package com.marsu.armuseumproject.fragments

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.marsu.armuseumproject.adapters.HomeRecyclerAdapter
import com.marsu.armuseumproject.database.ArtDB
import com.marsu.armuseumproject.database.Artwork
import com.marsu.armuseumproject.databinding.FragmentHomeBinding
import com.marsu.armuseumproject.viewmodels.HomeViewModel
import java.lang.reflect.Type

class HomeFragment : Fragment() {

    private lateinit var adapter: HomeRecyclerAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var viewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var lastFive = mutableListOf<Int>() // initiate variable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = HomeViewModel(requireActivity().application)
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        adapter = HomeRecyclerAdapter()
        adapter.setHasStableIds(true)
        layoutManager = LinearLayoutManager(activity)
        binding.homeRecycler.adapter = adapter
        binding.homeRecycler.setHasFixedSize(true)
        binding.homeRecycler.layoutManager = layoutManager

        // Retrieve lastFive from shared preferences and converting back to list from json
        val sharedPreferences = activity?.getPreferences(Context.MODE_PRIVATE)
        val json = sharedPreferences?.getString(SHARED_KEY, null)
        val type: Type = object : TypeToken<List<Int>>() {}.type
        if (json != null) {
            lastFive = Gson().fromJson<MutableList<Int>>(json, type)
        }

        // Getting the artwork objects by id's collected in lastFive and sending info to recycler adapter
        var collectedLastFive: List<Artwork> = listOf()
        for (i in lastFive.indices) {
            viewModel.getArt(lastFive[i]).observe(viewLifecycleOwner) { homoja ->
                homoja.let {
                    if (it != null) {
                        collectedLastFive += it
                    }
                    adapter.setData(collectedLastFive)
                }
            }
        }

        // TODO: Place the logic to the AR Selection
        // Initial logic for adding latest watched artwork id or uri
        // Checks if the id or uri already exist in the list
        // If it does, removes the old one from the collection before adding it again as the latest

        // TODO: Place the logic to the AR Selection
        // Logic to store list into shared preference
        // Accessing .getSharedPreferences function to retrieve or modify, requires context
        // Make the SHARED_KEY a const val outside the ar selection class, like request_code in sfg


        // TODO: Retrieve stored data from shared preferences and show in this view
        // First access to the shared preferences
        // Retrieve LAST_FIVE
        // Convert back to list
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}