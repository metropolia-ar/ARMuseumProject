package com.marsu.armuseumproject.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ArtworkDAO {
    // Adds arwork to DB
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addArtwork(art: Artwork)

    @Query("SELECT * FROM art_table")
    fun getAllArt(): LiveData<List<Artwork>>

    @Query("SELECT * FROM art_table WHERE objectID = :objectID")
    fun getSpecificArt(objectID: Int): LiveData<List<Artwork>>
}