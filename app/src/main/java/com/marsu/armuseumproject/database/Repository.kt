package com.marsu.armuseumproject.database

// The link between fragments/viewmodels and database.
class ArtRepository(private val artworkDAO: ArtworkDAO) {

    fun getAllFromDatabase() = artworkDAO.getAllArt()
    fun getOneFromDatabase(objectID: Int) = artworkDAO.getSpecificArt(objectID)
    fun getByDepartment(department: String) = artworkDAO.getArtByDepartment(department)

    /* TODO: adding art to db */
}