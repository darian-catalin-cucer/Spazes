package com.mcdev.spazes.service

import com.mcdev.spazes.dto.SpacesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SpacesApiService {

    @GET("/2/spaces/search")
    suspend fun getSpacesByTitle(
        @Header(value = "Authorization") token: String,
        @Query(value = "query") query: String,
        @Query(value = "space.fields") spaceFields: String,
        @Query(value = "user.fields") userFields: String,
        @Query(value = "expansions") expansions: String
    ): Response<SpacesResponse>

}