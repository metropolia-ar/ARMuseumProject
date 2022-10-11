package com.marsu.armuseumproject.activities

import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import com.marsu.armuseumproject.databinding.ActivitySelectDepartmentBinding
import com.marsu.armuseumproject.viewmodels.SelectDepartmentViewModel

/**
 * Displays a popup window which enables the user to select a Department to filter out the found Artworks from the API.
 */
class SelectDepartmentActivity : PopupActivity() {

    private lateinit var selectDepartmentViewModel: SelectDepartmentViewModel
    private lateinit var binding: ActivitySelectDepartmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySelectDepartmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        selectDepartmentViewModel = SelectDepartmentViewModel(this)
        binding.selectDepartmentViewModel = selectDepartmentViewModel
        binding.lifecycleOwner = this

        binding.departmentBackButton.setOnClickListener { finish() }
        binding.departmentResetButton.setOnClickListener {
            selectDepartmentViewModel.setSelectedDepartmentToPreferences()
            binding.departmentRadioGroup.clearCheck()
        }

        binding.departmentRadioGroup.setOnCheckedChangeListener { _, int ->
            selectDepartmentViewModel.setDepartmentInfo(int)
        }
    }

    override fun onResume() {
        super.onResume()

        try {
            val id = selectDepartmentViewModel.getSelectedDepartmentRadioButton()
            if (id != 0) findViewById<RadioButton>(id).isChecked = true
        } catch (e: Exception) {
            Log.e("Failed to set radioButton", e.message.toString())
        }
    }
}