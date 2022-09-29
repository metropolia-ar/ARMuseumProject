package com.marsu.armuseumproject.service

import com.google.gson.annotations.SerializedName
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

    data class Art(
        val objectID: Int,
        val primaryImage: String,
        val primaryImageSmall: String,
        val additionalImages: List<String>,
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
        ): Art

        /**
         * Returns all department ids and names.
         */
        @GET("departments")
        suspend fun getDepartments(): Departments

        @GET("departments")
        suspend fun getDepartmentArts()


        /**
         * Returns art IDs according to the query parameter.
         */
        @GET("search?")
        suspend fun getArtIDs(
            @Query("q") q: String
        ): SearchResult


    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseURL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: Service = retrofit.create(Service::class.java)
}


