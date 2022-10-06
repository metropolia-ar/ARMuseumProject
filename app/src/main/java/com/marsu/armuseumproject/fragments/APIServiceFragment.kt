package com.marsu.armuseumproject.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.marsu.armuseumproject.R
import com.marsu.armuseumproject.activities.PopupActivity
import com.marsu.armuseumproject.activities.SelectDepartmentActivity
import com.marsu.armuseumproject.adapters.ApiServiceAdapter
import com.marsu.armuseumproject.databinding.FragmentApiServiceBinding
import com.marsu.armuseumproject.viewmodels.ApiServiceViewModel

/**
 * Contains a EditText and RecyclerView for fetching and displaying found artwork from the API.
 */
class APIServiceFragment : Fragment() {

    private lateinit var apiServiceViewModel: ApiServiceViewModel
    private lateinit var binding: FragmentApiServiceBinding
    private lateinit var adapter: ApiServiceAdapter
    private lateinit var layoutManager : LinearLayoutManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Init VM
        apiServiceViewModel = ApiServiceViewModel(requireActivity())
        apiServiceViewModel.getArts(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Initialize databinding
        binding = FragmentApiServiceBinding.inflate(inflater)
        binding.apiServiceViewModel = apiServiceViewModel
        binding.lifecycleOwner = this

        // Recyclerview setup
        adapter = ApiServiceAdapter()
        adapter.setHasStableIds(true)
        layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = layoutManager


        // Search button
        binding.searchButton.setOnClickListener {
            apiServiceViewModel.searchArtsWithInput()
            val kb = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            kb.hideSoftInputFromWindow(view?.windowToken, 0)
        }

        // Department settings
        binding.openDepartmentSettings.setOnClickListener {
            val intent = Intent(activity, SelectDepartmentActivity::class.java)
            startActivity(intent)
        }

        // Clear button for department
        binding.resetDepartment.setOnClickListener {
            apiServiceViewModel.resetSelectedDepartment()
        }


        // Recyclerview updates when fetching data from API
        apiServiceViewModel.artsList.observe(viewLifecycleOwner) { arts ->
            arts.let {
                adapter.setData(it)
            }
        }

        apiServiceViewModel.initialBatchLoaded.observe(viewLifecycleOwner) {
            it.let {
                if (!it) {
                    binding.resultAmount.text = "Loading.."
                }
            }
        }


        // TODO: Set resultAmount.text at loadingResults. From there, get the resultAmount and set the text. Might want to move this logic to
        //  the VM as well.
        apiServiceViewModel.resultAmount.observe(viewLifecycleOwner) {
            it.let {
             if (it == 1) {
                    binding.resultAmount.text = "$it ${resources.getString(R.string.result)}"
                } else if (it > 1) {
                    binding.resultAmount.text = "$it ${resources.getString(R.string.results)}"
                } else {
                    binding.resultAmount.text = "${resources.getString(R.string.no_result)}"
                }
            }
        }

        apiServiceViewModel.departmentId.observe(viewLifecycleOwner) {
            it.let {
                if (it == 0) {
                    binding.departmentIndicator.visibility = View.GONE
                } else {
                    binding.departmentIndicator.visibility = View.VISIBLE
                }
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
                    binding.searchButton.isEnabled = false
                }
                else {
                    binding.progressBar.visibility = View.INVISIBLE
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.searchButton.isEnabled = true
                }
            }
        }



        return binding.root
    }


    override fun onResume() {
        super.onResume()
        apiServiceViewModel.updateDepartmentID()
    }
}