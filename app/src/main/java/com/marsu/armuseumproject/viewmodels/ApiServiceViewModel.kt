package com.marsu.armuseumproject.viewmodels

import android.util.Log
import android.widget.SearchView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.marsu.armuseumproject.database.Artwork
import com.marsu.armuseumproject.service.APIService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ApiServiceViewModel: ViewModel(), SearchView.OnQueryTextListener {

    private val initialBatchSize = 15
    private val service = APIService.service
    val searchInput = MutableLiveData("cat")
    private val departmentId = MutableLiveData(0)

    private val _foundIDs = MutableLiveData<MutableList<Int>>()

    private val _artsList = MutableLiveData(listOf<Artwork>())
    val artsList : LiveData<List<Artwork>>
        get() = _artsList

    private val _loadingResults = MutableLiveData(false)
    val loadingResults: LiveData<Boolean>
        get() = _loadingResults

    private val _initialBatchLoaded = MutableLiveData(true)
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


    /**
     * Fetch art data according to the stored IDs.
     */
    fun getArts(refresh: Boolean = false) {

        CoroutineScope(Dispatchers.Main).launch {

            if (_foundIDs.value == null || refresh) {
                _foundIDs.value = getArtIDs()
            }

            _loadingResults.value = true
            var x = 0

            if (_artsList.value == null || _artsList.value?.isEmpty() == true) {

                _resultAmount.value = 0
                _initialBatchLoaded.value = false
                while (x < initialBatchSize.coerceAtMost(_foundIDs.value?.size ?: 0)) {
                    if (addArtIfImagesAreFound()) x++
                }
                _resultAmount.value = _foundIDs.value?.size ?: 0

            } else {

                while (x < paginationAmount) {
                    if ((_artsList.value?.size ?: 0) >= (_foundIDs.value?.size ?: 0)) break
                    if (addArtIfImagesAreFound()) x++
                }

            }
            Log.d("artsList size", artsList.value?.size.toString())


            _loadingResults.value = false
            _initialBatchLoaded.value = true
        }
    }

    fun searchArtsWithInput() {
        if (searchInput.value?.isEmpty() == true) {
            // TODO: Optional validators for the search input, i.e. has to be more than 3 characters etc.
            return
        }
        _artsList.value = mutableListOf()
        getArts(true)
        Log.d("SearchInput value", searchInput.value.toString())
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

    /**
     * Returns all the found departments.
     */
 /*   fun getDepartments() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.getDepartments()
            Log.d("getDepartments", "fetch ended $response")
        }
    }*/



    override fun onQueryTextSubmit(p0: String?): Boolean {
        searchInput.value = p0
        return true
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        searchInput.value = p0
        return true
    }
}