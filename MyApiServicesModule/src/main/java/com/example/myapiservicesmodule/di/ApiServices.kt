package com.example.myapiservicesmodule.di

import com.example.myapiservicesmodule.di.models.ResponseMovieDetails
import com.example.myapiservicesmodule.di.models.ResponseMovieList
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServices {
    @GET("/")
    suspend fun getMovieList(
        @Query("s") key: String,
        @Query("type") movie_type: String,
        @Query("page") page: Int,
        @Query("apikey") apikey: String
    ): ResponseMovieList

    @GET("/")
    suspend fun getMovieDetails(
        @Query("i") id: String?,
        @Query("apikey") apikey: String
    ): ResponseMovieDetails
}