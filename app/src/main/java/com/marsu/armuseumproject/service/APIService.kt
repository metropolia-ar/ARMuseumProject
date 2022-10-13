package com.marsu.armuseumproject.service

import com.marsu.armuseumproject.database.Artwork
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.net.URL

/**
 * Provides a Service, which contains the functionality for making queries to the API.
 * @see <a href="https://metmuseum.github.io/">The Metropolitan Museum of Art Collection API</a>
 */
object APIService {

    data class SearchResult(val total: String, val objectIDs: MutableList<Int>)

    private val baseURL = URL("https://collectionapi.metmuseum.org/public/collection/v1/")

    interface Service {

        /**
         * Fetches Artwork data according to the give objectID.
         * @return Artwork object
         */
        @GET("objects/{objectID}")
        suspend fun getObjectByID(
            @Path(value = "objectID") objectID: Int
        ): Artwork

        /**
         * Fetches ids which contain a valid image URL, is public and is a painting.
         * @return SearchResult object. Contains a list of Artwork ids.
         */
        @GET("search?")
        suspend fun getArtIDs(
            @Query("isOnView") isOnView: Boolean = true,
            @Query("hasImages") hasImages: Boolean = true,
            @Query("medium") medium: String = "Paintings",
            @Query("q") q: String
        ): SearchResult

        /**
         * Fetches ids which contain a valid image URL, is public, is a painting and contains the wanted departmentId.
         * @return SearchResult object. Contains a list of Artwork ids.
         */
        @GET("search?")
        suspend fun getArtIDs(
            @Query("isOnView") isOnView: Boolean = true,
            @Query("hasImages") hasImages: Boolean = true,
            @Query("medium") medium: String = "Paintings",
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


