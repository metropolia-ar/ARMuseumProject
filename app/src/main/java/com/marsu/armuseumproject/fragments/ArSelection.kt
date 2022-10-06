package com.marsu.armuseumproject.fragments

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
import com.marsu.armuseumproject.adapters.ArSelectionAdapter
import com.marsu.armuseumproject.R
import com.marsu.armuseumproject.databinding.FragmentArSelectionBinding
import com.marsu.armuseumproject.viewmodels.ArSelectionViewModel

class ArSelection : Fragment() {

    private lateinit var arSelectionViewModel: ArSelectionViewModel
    private lateinit var adapter: ArSelectionAdapter
    private lateinit var layoutManager : LinearLayoutManager
    private var _binding: FragmentArSelectionBinding? = null;
    private val binding get() = _binding!!

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

        binding.startArButton.setOnClickListener { navigateToArActivity(view) }

        adapter = ArSelectionAdapter()
        adapter.setHasStableIds(true)
        layoutManager = LinearLayoutManager(activity)
        adapter.onItemClick = { artwork ->
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