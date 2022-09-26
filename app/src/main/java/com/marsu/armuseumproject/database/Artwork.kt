package com.marsu.armuseumproject.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "art_table")
data class Artwork(
    @PrimaryKey(autoGenerate = false)
    val objectID: Int,
    val primaryImage: String,
    val title: String,
    val department: String
)
