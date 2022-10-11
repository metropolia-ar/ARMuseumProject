package com.marsu.armuseumproject.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.marsu.armuseumproject.MyApp
import com.marsu.armuseumproject.adapters.ArSelectionAdapter
import com.marsu.armuseumproject.R
import com.marsu.armuseumproject.databinding.FragmentArSelectionBinding
import com.marsu.armuseumproject.viewmodels.ArSelectionViewModel
import java.lang.reflect.Type

const val SHARED_KEY = "LAST_FIVE"

class ArSelection : Fragment() {

    private lateinit var arSelectionViewModel: ArSelectionViewModel
    private lateinit var adapter: ArSelectionAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private var _binding: FragmentArSelectionBinding? = null;
    private val binding get() = _binding!!

    private var lastFive = mutableListOf<Int>() // initiate variable


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

        val sharedPreferences = activity?.getPreferences(Context.MODE_PRIVATE)
        val json = sharedPreferences?.getString(SHARED_KEY, null)
        val type: Type = object : TypeToken<List<Int>>() {}.type
        if (json != null) {
            lastFive = Gson().fromJson<MutableList<Int>>(json, type)
        }

        binding.startArButton.setOnClickListener { navigateToArActivity(view) }

        adapter = ArSelectionAdapter()
        adapter.setHasStableIds(true)
        layoutManager = LinearLayoutManager(activity)
        adapter.onItemClick = { artwork ->


            if (!lastFive.contains(artwork.objectID) || lastFive.isEmpty()) {
                lastFive.add(
                    0,
                    artwork.objectID
                ) // add the newest to the first element of list, previous ones get pushed one more forward
            } else {
                val index = lastFive.indexOf(artwork.objectID)
                lastFive.drop(index)
                lastFive.add(
                    0,
                    artwork.objectID
                ) // add the newest only after dropping out the very same id or int from the list
            }

            if (lastFive.size > 5) {
                lastFive.removeLast() // keeps only 5 most recent in the list
            }

            val storedLastFive = Gson().toJson(lastFive) // convert list to storable json
            val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
            if (sharedPref != null) {
                with(sharedPref.edit()) {
                    putString(SHARED_KEY, storedLastFive)
                    apply()
                }
            }

            binding.chosenTitle.text = artwork.title
            binding.chosenArtist.text = artwork.artistDisplayName
            arSelectionViewModel.imageUri.postValue(artwork.primaryImage.toUri())
        }
        binding.arSelectionRecyclerview.adapter = adapter
        binding.arSelectionRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.arSelectionRecyclerview.setHasFixedSize(true)
        binding.arSelectionRecyclerview.layoutManager = layoutManager
        arSelectionViewModel.getAllArtwork.value?.let { adapter.setData(it) }

        // Recyclerview updates when fetching data from Room
        arSelectionViewModel.getAllArtwork.observe(viewLifecycleOwner) { arts ->
            arts.let {
                if (it != null) {
                    adapter.setData(it)
                }
            }
        }

        return view
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