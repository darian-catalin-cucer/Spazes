package com.mcdev.spazes.service

import com.mcdev.twitterapikit.response.SpaceListResponse
import com.mcdev.twitterapikit.response.SpaceSingleResponse
import com.mcdev.twitterapikit.response.UserListResponse
import com.mcdev.twitterapikit.response.UserSingleResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface TwitterApiService {

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

    /*Get spaces created by signed in user*/
    @GET("/2/spaces/by/creator_ids")
    suspend fun getSpacesByCreatorIds(
        @Header(value = "Authorization") token: String,
        @Query(value = "user_ids") user_ids: String,
        @Query(value = "space.fields") spaceFields: String,
        @Query(value = "user.fields") userFields: String,
        @Query(value = "expansions") expansions: String,
        @Query(value = "topic.fields") topicFields: String
    ): Response<SpaceListResponse>


    /*Get users by username*/
    @GET("/2/users/by/username/{username}")
    suspend fun getUsersByUsername(
        @Header(value = "Authorization") token: String,
        @Path(value = "username") username: String,
        @Query(value = "expansions") expansions: String,
        @Query(value = "tweet.fields") tweetFields: String,
        @Query(value = "user.fields") userFields: String
    ): Response<UserSingleResponse>


    /*Get users by list of usernames*/
    @GET(value = "/2/users/by")
    suspend fun getUsersByUsernames(
        @Header(value = "Authorization") token: String,
        @Query(value = "usernames") usernames: String,
        @Query(value = "expansions") expansions: String,
        @Query(value = "tweet.fields") tweetFields: String,
        @Query(value = "user.fields") userFields: String
    ): Response<UserListResponse>

    /*Get users by list of ids*/
    @GET(value = "/2/users")
    suspend fun getUsersByIds(
        @Header(value = "Authorization") token: String,
        @Query(value = "ids") ids: String,
        @Query(value = "expansions") expansions: String,
        @Query(value = "tweet.fields") tweetFields: String,
        @Query(value = "user.fields") userFields: String
    ):Response<UserListResponse>
}