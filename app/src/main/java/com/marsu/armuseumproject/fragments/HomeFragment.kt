package com.marsu.armuseumproject.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.google.gson.Gson
import com.marsu.armuseumproject.R
import com.marsu.armuseumproject.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

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

        var idoruri = 0 // placeholder for the actual data to be stored
        val lastFive = mutableListOf<Int>() // initiate variable
        if (!lastFive.contains(idoruri) || lastFive.isEmpty()) {
            lastFive.add(0, idoruri) // add the newest to the first element of list, previous ones get pushed one more forward
        } else {
            val index = lastFive.indexOf(idoruri)
            lastFive.drop(index)
            lastFive.add(0, idoruri) // add the newest only after dropping out the very same id or int from the list
        }

        if (lastFive.size > 5) {
            lastFive.removeLast() // keeps only 5 most recent in the list
        }

        // TODO: Place the logic to the AR Selection
        // Logic to store list into shared preference
        // Accessing .getSharedPreferences function to retrieve or modify, requires context
        // Make the SHARED_KEY a const val outside the ar selection class, like request_code in sfg
        val SHARED_KEY = "LAST_FIVE"
        var storedLastFive = Gson().toJson(lastFive) // convert list to storable json
        val sharedPreferences: SharedPreferences? = context?.getSharedPreferences("PREFERENCE_NAME", 0) // remove nullable when in correct place
        val editor = sharedPreferences?.edit()
        editor?.putString(SHARED_KEY,storedLastFive)
        editor?.apply()

        // TODO: Retrieve stored data from shared preferences and show in this view
        // First access to the shared preferences
        // Retrieve LAST_FIVE
        // Convert back to list
        
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