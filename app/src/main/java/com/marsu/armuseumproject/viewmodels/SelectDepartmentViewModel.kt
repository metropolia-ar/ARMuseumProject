package com.marsu.armuseumproject.viewmodels

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.marsu.armuseumproject.R
import com.marsu.armuseumproject.activities.SelectDepartmentActivity

/**
 * ViewModel for SelectDepartmentActivity. Provides functionality for selecting a department and saving it to SharedPreferences.
 */
class SelectDepartmentViewModel(val context: SelectDepartmentActivity) : ViewModel() {

    /**
     * Set selected department
     */
    fun setSelectedDepartmentToPreferences(id: Int = 0, btnId: Int = 0, name: String = "") {
        val pref: SharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putInt("selectedDepartment", id)
        editor.putInt("selectedDepartmentRadioButton", btnId)
        editor.putString("selectedDepartmentName", name)
        editor.apply()
    }

    /**
     * Return selected department name
     */
    fun getSelectedDepartmentRadioButton(): Int {
        val pref: SharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        return pref.getInt("selectedDepartmentRadioButton", 0)
    }

    /**
     * Sets the selected department info to SharedPreferences.
     */
    fun setDepartmentInfo(int: Int) {

        fun setToPreferences(id: Int, nameId: Int) {
            setSelectedDepartmentToPreferences(id, int, context.getString(nameId))
        }

        when (int) {
            R.id.dep1 -> setToPreferences(1, R.string.dep1)
            R.id.dep3 -> setToPreferences(3, R.string.dep3)
            R.id.dep4 -> setToPreferences(4, R.string.dep4)
            R.id.dep5 -> setToPreferences(5, R.string.dep5)
            R.id.dep6 -> setToPreferences(6, R.string.dep6)
            R.id.dep7 -> setToPreferences(7, R.string.dep7)
            R.id.dep8 -> setToPreferences(8, R.string.dep8)
            R.id.dep9 -> setToPreferences(9, R.string.dep9)
            R.id.dep10 -> setToPreferences(10, R.string.dep10)
            R.id.dep11 -> setToPreferences(11, R.string.dep11)
            R.id.dep12 -> setToPreferences(12, R.string.dep12)
            R.id.dep13 -> setToPreferences(13, R.string.dep13)
            R.id.dep14 -> setToPreferences(14, R.string.dep14)
            R.id.dep15 -> setToPreferences(15, R.string.dep15)
            R.id.dep16 -> setToPreferences(16, R.string.dep16)
            R.id.dep17 -> setToPreferences(17, R.string.dep17)
            R.id.dep18 -> setToPreferences(18, R.string.dep18)
            R.id.dep19 -> setToPreferences(19, R.string.dep19)
            R.id.dep21 -> setToPreferences(21, R.string.dep21)
        }
    }
}