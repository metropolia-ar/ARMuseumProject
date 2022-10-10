package com.marsu.armuseumproject.viewmodels

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.marsu.armuseumproject.MyApp
import com.marsu.armuseumproject.R
import com.marsu.armuseumproject.database.Artwork
import com.marsu.armuseumproject.service.APIService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ApiServiceViewModel(val context: Context): ViewModel() {

    private val initialBatchSize = 15
    private val service = APIService.service
    val searchInput = MutableLiveData("cat")

    private val _departmentText = MutableLiveData("")
    val departmentText : LiveData<String>
            get() = _departmentText

    private val _resultText = MutableLiveData("")
    val resultText : LiveData<String>
        get() = _resultText

    private val _departmentId = MutableLiveData(0)
    val departmentId : LiveData<Int>
        get() = _departmentId

    private val _foundIDs = MutableLiveData<MutableList<Int>>()

    private val _artsList = MutableLiveData(listOf<Artwork>())
    val artsList : LiveData<List<Artwork>>
        get() = _artsList

    private val _loadingResults = MutableLiveData(true)
    val loadingResults: LiveData<Boolean>
        get() = _loadingResults

    private val _initialBatchLoaded = MutableLiveData(false)
    val initialBatchLoaded : LiveData<Boolean>
        get() = _initialBatchLoaded

    private val _resultAmount = MutableLiveData(0)
    val resultAmount : LiveData<Int>
        get() = _resultAmount

    val paginationAmount = 10

    /**
     * Get Art ids and store them for later usage.
     */
    private suspend fun getArtIDs(): MutableList<Int> {

        return if (searchInput.value?.isNotEmpty() == true) {

            val response = if (departmentId.value != 0) {
                service.getArtIDs(q = searchInput.value.toString(), departmentId = departmentId.value ?: 0)
            } else {
                service.getArtIDs(q = searchInput.value.toString())
            }

            if (response.objectIDs.isNullOrEmpty()) {
                Log.d("getArtIDs", "No objectIDs found")
            } else {
                Log.d("getArtIDs", "Found ${response.objectIDs.size} ids")
            }

            response.objectIDs

        } else {
            mutableListOf()
        }
    }

    fun updateDepartmentID() {
        val pref: SharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val newDep = pref.getInt("selectedDepartment", 0)
        if (newDep != departmentId.value) {
            _departmentId.value = newDep
            getArts(true)
        } else {
            _departmentId.value = newDep
        }
        updateDepartmentName()
    }


    /**
     * Fetch art data according to the stored IDs.
     */
    fun getArts(refresh: Boolean = false) {

        CoroutineScope(Dispatchers.Main).launch {

            if (_foundIDs.value == null || refresh) {
                _foundIDs.value = getArtIDs()
                _artsList.value = emptyList()
            }
            _loadingResults.value = true

            if (_artsList.value == null || _artsList.value?.isEmpty() == true) {

                _resultAmount.value = 0
                if (_initialBatchLoaded.value != false) _initialBatchLoaded.value = false


                for (i in  1..initialBatchSize.coerceAtMost(_foundIDs.value?.size?.minus(1) ?: 0)) {
                    addArtIfImagesAreFound()
                }
                _resultAmount.value = _foundIDs.value?.size ?: 0

            } else {

                for (i in 1..paginationAmount) {
                    if ((_artsList.value?.size ?: 0) >= (_foundIDs.value?.size ?: 0)) break
                    addArtIfImagesAreFound()
                }
            }

            Log.d("artsList size", artsList.value?.size.toString())

            _loadingResults.value = false
            _initialBatchLoaded.value = true
        }
    }

    fun searchArtsWithInput() {
        if (searchInput.value?.isEmpty() == true) {
            return
        } else if (searchInput.value != null && searchInput.value?.length!! < 2) {
            Toast.makeText(MyApp.appContext, "${MyApp.appContext.getString(R.string.search_too_short)}.", Toast.LENGTH_SHORT).show()
            return
        }
        _artsList.value = mutableListOf()
        getArts(true)
        Log.d("SearchInput value", searchInput.value.toString())
    }

    fun resetSelectedDepartment() {
        val pref: SharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putInt("selectedDepartment", 0)
        editor.putInt("selectedDepartmentRadioButton", 0)
        editor.putString("selectedDepartmentName", "")
        editor.apply()
        updateDepartmentID()
        getArts(true)
    }

    fun updateResultText() {

        val r = resultAmount.value

        if (initialBatchLoaded.value != true) {
            _resultText.value = context.getString(R.string.searching)
            return
        } else if (r == 1) {
            _resultText.value = "$r ${context.getString(R.string.result)}"
        } else if (r != null) {
            if (r > 1) {
                _resultText.value = "$r ${context.getString(R.string.results)}"
            } else {
                _resultText.value = context.getString(R.string.no_result)
            }
        }
    }


    private fun updateDepartmentName() {
        val pref: SharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        _departmentText.value = pref.getString("selectedDepartmentName", "")
    }

    /**
     * Adds the found art to the list if it contains the required primary images. If it lacks images, the ID is removed from the list.
     */
    private suspend fun addArtIfImagesAreFound(): Boolean {

        if ((_artsList.value?.size ?: 0) >= (_foundIDs.value?.size ?: 0)) return false

        val objectID = _foundIDs.value?.get(
            (_artsList.value?.size ?: 0).coerceAtMost(_foundIDs.value?.size?.minus(1) ?: 0)
        ) ?: 0

        try {

            val art = service.getObjectByID(objectID)

            if (isValidArt(art)) {
                _artsList.value = _artsList.value.orEmpty() + art
                return true
            } else {
                _foundIDs.value?.remove(objectID)
                Log.d("Empty art removed", "id: $objectID")
            }
        }
        catch (e: HttpException) {
            Log.d("HttpException @ addArtIfImagesAreFound, removing id $objectID", e.message.toString())
            _foundIDs.value?.remove(objectID)
        }
        catch (e: Exception) {
            Log.d("Exception @ addArtIfImagesAreFound", e.message.toString())
        }
        return false
    }

    /**
     * True if contains the wanted data.
     */
    private fun isValidArt(art: Artwork): Boolean {
        return art.primaryImage.isNotEmpty() &&
                art.primaryImageSmall.isNotEmpty()
    }



}