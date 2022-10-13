package com.marsu.armuseumproject.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Database initialization and link for accessing DAO.
 */
@Database(entities = [Artwork::class], version = 2)
abstract class ArtDB : RoomDatabase() {
    abstract fun artDao(): ArtworkDAO

    companion object {
        private var INSTANCE: ArtDB? = null

        @Synchronized
        fun get(context: Context): ArtDB {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    ArtDB::class.java, "artdb"
                ).fallbackToDestructiveMigration().build()
            }
            return INSTANCE!!
        }
    }
}