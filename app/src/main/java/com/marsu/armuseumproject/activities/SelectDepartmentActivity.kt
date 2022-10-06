package com.marsu.armuseumproject.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.compose.ui.res.stringResource
import androidx.core.view.children
import com.gorisse.thomas.sceneform.util.getResourceUri
import com.marsu.armuseumproject.R
import com.marsu.armuseumproject.databinding.ActivitySelectDepartmentBinding
import com.marsu.armuseumproject.service.APIService
import com.marsu.armuseumproject.viewmodels.SelectDepartmentViewModel

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