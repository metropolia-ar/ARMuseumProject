package com.marsu.armuseumproject.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.marsu.armuseumproject.adapters.ApiServiceAdapter
import com.marsu.armuseumproject.databinding.FragmentApiServiceBinding
import com.marsu.armuseumproject.viewmodels.ApiServiceViewModel

/**
 * Contains a SearchView and RecyclerView for fetching and displaying found artwork from the API.
 */
class APIServiceFragment : Fragment() {

    private lateinit var apiServiceViewModel: ApiServiceViewModel
    private lateinit var binding: FragmentApiServiceBinding
    private lateinit var adapter: ApiServiceAdapter
    private lateinit var layoutManager : LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Initialize VM and databinding
        apiServiceViewModel = ApiServiceViewModel()
        binding = FragmentApiServiceBinding.inflate(inflater)
        binding.apiServiceViewModel = apiServiceViewModel
        binding.apiSearchInput.setOnQueryTextListener(apiServiceViewModel)

        // Recyclerview setup
        adapter = ApiServiceAdapter()
        layoutManager = LinearLayoutManager(activity)

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = layoutManager

        binding.button2.setOnClickListener {
            apiServiceViewModel.searchArtsWithInput()
        }

        apiServiceViewModel.getArts(false)
        apiServiceViewModel.artsList.observe(viewLifecycleOwner) { arts ->
            arts.let {
                Log.d("observing", it.toString())
                adapter.setData(it)
            }
        }

        // RecyclerView pagination
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (layoutManager.findLastVisibleItemPosition() >=
                    (apiServiceViewModel.artsList.value?.size?.minus(apiServiceViewModel.paginationAmount) ?: 0)) {

                    if ((apiServiceViewModel.loadingResults.value == false)) {
                        apiServiceViewModel.getArts(false)
                    }
                }

            }
        })


        apiServiceViewModel.loadingResults.observe(viewLifecycleOwner) {status ->
            status.let {
                if (it) binding.progressBar.visibility = View.VISIBLE
                else binding.progressBar.visibility = View.INVISIBLE
            }
        }


        return binding.root
    }

}