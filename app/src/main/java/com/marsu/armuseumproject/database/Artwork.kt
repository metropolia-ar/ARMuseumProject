package com.marsu.armuseumproject.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "art_table")
data class Artwork(
    @PrimaryKey(autoGenerate = false)
    val objectID: Int,
    val primaryImage: String,
    val primaryImageSmall: String,
    val department: String,
    val objectName: String,
    val title: String,
    val culture: String,
    val artistDisplayName: String,
    val artistDisplayBio: String,
    val artistBeginDate: String,
    val artistEndDate: String,
    val dimensions: String
)
