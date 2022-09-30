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
    val title: String,
    val artistDisplayName: String,
    val dimensions: String,
    val classification: String
)
