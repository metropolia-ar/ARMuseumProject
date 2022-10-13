package com.marsu.armuseumproject.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.marsu.armuseumproject.R
import com.marsu.armuseumproject.adapters.HomeRecyclerAdapter
import com.marsu.armuseumproject.database.Artwork
import com.marsu.armuseumproject.databinding.FragmentHomeBinding
import com.marsu.armuseumproject.viewmodels.HomeViewModel
import java.lang.reflect.Type

/**
 * Contains 2 TextViews and RecyclerView.
 * TextViews display a welcome and when first used, short instructions to using the app.
 * After user has tried some artworks on AR, the instructions change to describing below elements to be recently viewed art.
 * RecyclerView displays 5 artworks that have been tried out in AR most recently.
 */
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

        // Checking if lastFive has anything in it
        // If not, showing instructions text instead of most recents.
        if (lastFive.isEmpty()) {
            binding.homeIntro.text = getString(R.string.home_intro)
        }

        // Getting the artwork objects by id's collected in lastFive and sending info to recycler adapter
        var collectedLastFive: List<Artwork> = listOf()
        for (i in lastFive.indices) {
            viewModel.getArt(lastFive[i]).observe(viewLifecycleOwner) { item ->
                item.let {
                    if (it != null) {
                        collectedLastFive += it
                    }
                    adapter.setData(collectedLastFive)
                }
            }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}