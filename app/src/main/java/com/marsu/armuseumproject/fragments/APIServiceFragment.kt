package com.marsu.armuseumproject.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
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
        adapter.setHasStableIds(true)
        layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = layoutManager
        apiServiceViewModel.getArts(false)

        // Search button
        binding.button2.setOnClickListener {
            apiServiceViewModel.searchArtsWithInput()
            val kb = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            kb.hideSoftInputFromWindow(view?.windowToken, 0)
        }

        // Recyclerview updates when fetching data from API
        apiServiceViewModel.artsList.observe(viewLifecycleOwner) { arts ->
            arts.let {
                adapter.setData(it)
            }
        }

        // RecyclerView pagination
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (layoutManager.findLastCompletelyVisibleItemPosition() >=
                    (apiServiceViewModel.artsList.value?.size?.minus(apiServiceViewModel.paginationAmount) ?: 0)) {

                    if ((apiServiceViewModel.loadingResults.value == false)) {
                        apiServiceViewModel.getArts(false)
                    }
                }

            }
        })

        // ProgressBar & recyclerview invisibility while loading
        apiServiceViewModel.initialBatchLoaded.observe(viewLifecycleOwner) {status ->
            status.let {
                if (!it) {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.INVISIBLE
                    binding.button2.isEnabled = false
                }
                else {
                    binding.progressBar.visibility = View.INVISIBLE
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.button2.isEnabled = true
                }
            }
        }


        return binding.root
    }

}