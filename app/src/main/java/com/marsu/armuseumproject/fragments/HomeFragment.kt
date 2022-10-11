package com.marsu.armuseumproject.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.marsu.armuseumproject.MyApp
import com.marsu.armuseumproject.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val lastFive = mutableListOf<Int>() // initiate variable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

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
        val sharedPreferences = activity?.getPreferences(Context.MODE_PRIVATE)
        if (sharedPreferences != null) {
            val haettu = sharedPreferences.getString(SHARED_KEY, "storedLastFive")
            if (haettu != null) {
                Log.d("TESTING", haettu)
            }
        }


        // TODO: Selvitä miten näytät listasta haetut tiedot
        // Recyclerin todennäköisesti tarttee, koska pienemmällä näytöllä voi tarvita scrollable ominaisuuden
        // Mutta miten recyclerin kanssa saa alkuun näytettyä vähemmän kuin 5 viimeisintä?

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}