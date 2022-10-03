package com.marsu.armuseumproject.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.view.children
import com.marsu.armuseumproject.R
import com.marsu.armuseumproject.databinding.ActivitySelectDepartmentBinding
import com.marsu.armuseumproject.service.APIService
import com.marsu.armuseumproject.viewmodels.SelectDepartmentViewModel

class SelectDepartmentActivity : PopupActivity() {

    private lateinit var selectDepartmentViewModel: SelectDepartmentViewModel
    private lateinit var binding: ActivitySelectDepartmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_department)

        binding = ActivitySelectDepartmentBinding.inflate(layoutInflater)
        selectDepartmentViewModel = SelectDepartmentViewModel(this)
        binding.selectDepartmentViewModel = selectDepartmentViewModel

        findViewById<RadioGroup>(R.id.department_radio_group).setOnCheckedChangeListener { _, int ->

            var id = when (int) {
                R.id.dep1 -> 1
                R.id.dep3 -> 3
                R.id.dep4 -> 4
                R.id.dep5 -> 5
                R.id.dep6 -> 6
                R.id.dep7 -> 7
                R.id.dep8 -> 8
                R.id.dep9 -> 9
                R.id.dep10 -> 10
                R.id.dep11 -> 11
                R.id.dep12 -> 12
                R.id.dep13 -> 13
                R.id.dep14 -> 14
                R.id.dep15 -> 15
                R.id.dep16 -> 16
                R.id.dep17 -> 17
                R.id.dep18 -> 18
                R.id.dep19 -> 19
                R.id.dep21 -> 21
                else -> 0
            }

            selectDepartmentViewModel.setSelectedDepartmentToPreferences(id, int)
        }
    }

    override fun onResume() {
        super.onResume()

        val id = selectDepartmentViewModel.getSelectedDepartmentRadioButton()
        if (id != 0) findViewById<RadioButton>(id).isChecked = true

    }
}