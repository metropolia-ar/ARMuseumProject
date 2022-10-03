package com.marsu.armuseumproject.viewmodels

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import com.marsu.armuseumproject.activities.SelectDepartmentActivity


class SelectDepartmentViewModel(val context: SelectDepartmentActivity): ViewModel() {



    /**
     * Set selected department
     */
    fun setSelectedDepartmentToPreferences(id: Int, btnId: Int) {
        val pref: SharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putInt("selectedDepartment", id)
        editor.putInt("selectedDepartmentRadioButton", btnId)
        editor.apply()

        Log.d("Added to pref, checking", "department ${getSelectedDepartment()}")
    }

    /**
     * Return selected department
     */
    fun getSelectedDepartment(): Int {
        val pref: SharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        return pref.getInt("selectedDepartment", 0)
    }

    /**
     * Return selected department
     */
    fun getSelectedDepartmentRadioButton(): Int {
        val pref: SharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        return pref.getInt("selectedDepartmentRadioButton", 0)
    }

}