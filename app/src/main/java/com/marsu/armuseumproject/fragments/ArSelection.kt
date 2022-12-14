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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.marsu.armuseumproject.adapters.ArSelectionAdapter
import com.marsu.armuseumproject.databinding.FragmentArSelectionBinding
import com.marsu.armuseumproject.viewmodels.ArSelectionViewModel
import java.lang.reflect.Type

const val SHARED_KEY = "LAST_FIVE"

/**
 * Fragment for selecting artwork to be displayed in AR mode. Displays all the artwork saved to the Room database
 * in a RecyclerView. When an artwork has been selected, the 'Start AR' button can be used to navigate to the AR
 * activity with the image uri coming along as a navigation argument. Also saves the artwork to the most recent
 * artworks when navigating to AR mode.
 */

class ArSelection : Fragment() {

    private lateinit var arSelectionViewModel: ArSelectionViewModel
    private lateinit var adapter: ArSelectionAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private var _binding: FragmentArSelectionBinding? = null;
    private val binding get() = _binding!!
    private var lastFive = mutableListOf<Int>() // initiate variable

    private val args: ArSelectionArgs by navArgs()

    // Handle possible navigation arguments if coming via the recent artworks list from Home Fragment
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val chosenArtwork = args.latestArtwork
        if (chosenArtwork != null) {
            binding.chosenTitle.text = chosenArtwork.title
            binding.chosenArtist.text = chosenArtwork.artistDisplayName
            arSelectionViewModel.imageUri.postValue(chosenArtwork.primaryImage.toUri())
            arSelectionViewModel.imageId.postValue(chosenArtwork.objectID)
        }
    }

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

        // Retrieving the previously stored list of id's to use it as a base for lastFive
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
            binding.chosenTitle.text = artwork.title
            binding.chosenArtist.text = artwork.artistDisplayName
            arSelectionViewModel.imageUri.postValue(artwork.primaryImage.toUri())
            arSelectionViewModel.imageId.postValue(artwork.objectID)
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

    // Navigate to the AR Activity with the selected artwork's image URI as a navigation argument,
    // and also saves the selected artwork to the most recent list
    private fun navigateToArActivity(v: View) {
        val id = arSelectionViewModel.imageId.value
        if (id != null) {
            addToList(id)
            addToSharedPrefs()
        }
        val uri = arSelectionViewModel.imageUri.value.toString()
        val action = ArSelectionDirections.actionArSelectionToArActivity(uri)
        v.findNavController().navigate(action)
    }

    // Saving latest watched artwork id into MutableList<Int>
    // Checks if the id already exist in the list
    // If it does, removes the old one from the collection before adding it again as the latest
    private fun addToList(id: Int) {
        if (lastFive.contains(id)) {
            val index = lastFive.indexOf(id)
            lastFive.removeAt(index)
        }

        lastFive.add(0, id)

        // Keeps only 5 most recent in the list
        if (lastFive.size > 5) {
            lastFive.removeLast()
        }
    }

    // Converts lastFive (list of Artwork id's) to a json and stores to shared preferences
    private fun addToSharedPrefs() {
        val storedLastFive = Gson().toJson(lastFive)
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        if (sharedPref != null) {
            with(sharedPref.edit()) {
                putString(SHARED_KEY, storedLastFive)
                apply()
            }
        }
    }

}
