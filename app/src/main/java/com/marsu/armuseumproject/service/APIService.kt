package com.marsu.armuseumproject.service

import com.google.gson.annotations.SerializedName
import com.marsu.armuseumproject.database.Artwork
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.net.URL
import java.util.*


object APIService {

    data class Department(val departmentId: Int, val displayName: String)
    data class Departments(val departments: List<Department>)
    data class SearchResult(val total: String, val objectIDs: MutableList<Int>)

    private val baseURL = URL("https://collectionapi.metmuseum.org/public/collection/v1/")

    interface Service {

        /**
         * Returns all the available object (art) ids as List<Int>.
         */
        @GET("objects")
        suspend fun getObjectIDs() : List<Int>

        /**
         * Returns art data according to the given id.
         */
        @GET("objects/{objectID}")
        suspend fun getObjectByID(
            @Path(value = "objectID") objectID : Int
        ): Artwork

        /**
         * Returns all department ids and names.
         */
        @GET("departments")
        suspend fun getDepartments(): Departments

        @GET("departments")
        suspend fun getDepartmentArts()

        /**
         * Returns paintings according to the given query parameter.
         */
        @GET("search?")
        suspend fun getArtIDs(
            @Query("isOnView") isOnView : Boolean = true,
            @Query("hasImages") hasImages : Boolean = true,
            @Query("medium") medium : String = "Paintings",
            @Query("q") q: String
        ): SearchResult

        /**
         * Returns paintings according to the given query parameter and departmentId.
         */
        @GET("search?")
        suspend fun getArtIDs(
            @Query("isOnView") isOnView : Boolean = true,
            @Query("hasImages") hasImages : Boolean = true,
            @Query("medium") medium : String = "Paintings",
            @Query("q") q: String,
            @Query("departmentId") departmentId: Int
        ): SearchResult

    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseURL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: Service = retrofit.create(Service::class.java)
}


