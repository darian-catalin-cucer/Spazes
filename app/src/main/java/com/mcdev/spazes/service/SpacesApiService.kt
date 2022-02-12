package com.mcdev.spazes.service

import com.mcdev.twitterapikit.response.SpaceListResponse
import com.mcdev.twitterapikit.response.SpaceSingleResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface SpacesApiService {

    /*Get spaces by title*/
    @GET("/2/spaces/search")
    suspend fun getSpacesByTitle(
        @Header(value = "Authorization") token: String,
        @Query(value = "query") query: String,
        @Query(value = "space.fields") spaceFields: String,
        @Query(value = "user.fields") userFields: String,
        @Query(value = "expansions") expansions: String,
        @Query(value = "topic.fields") topicFields: String
    ): Response<SpaceListResponse>

    /*Get spaces by list of IDS*/
    @GET("/2/spaces")
    suspend fun getSpacesByIds(
        @Header(value = "Authorization") token: String,
        @Query(value = "ids") query: String,
        @Query(value = "space.fields") spaceFields: String,
        @Query(value = "user.fields") userFields: String,
        @Query(value = "expansions") expansions: String,
        @Query(value = "topic.fields") topicFields: String
    ): Response<SpaceListResponse>

    /*Get spaces by ID*/
    @GET("/2/spaces/{id}")
    suspend fun getSpacesById(
        @Header(value = "Authorization") token: String,
        @Path(value = "id") id: String,
        @Query(value = "space.fields") spaceFields: String,
        @Query(value = "user.fields") userFields: String,
        @Query(value = "expansions") expansions: String,
        @Query(value = "topic.fields") topicFields: String
    ): Response<SpaceSingleResponse>
}