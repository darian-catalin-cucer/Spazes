package com.mcdev.spazes.repository

import com.mcdev.spazes.service.TwitterApiService
import com.mcdev.spazes.util.Resource
import com.mcdev.twitterapikit.response.SpaceListResponse
import com.mcdev.twitterapikit.response.SpaceSingleResponse
import java.lang.Exception
import javax.inject.Inject

class SpacesRepository @Inject constructor(private val twitterApiService: TwitterApiService) : MainRepository {
    /*search spaces by title*/
    override suspend fun getSpacesByTitle(
        token: String,
        query: String,
        spaceFields: String,
        userFields: String,
        expansions: String,
        topicFields: String
    ): Resource<SpaceListResponse> {

        return try {
            val response = twitterApiService.getSpacesByTitle(token, query, spaceFields, userFields ,expansions, topicFields)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                Resource.Success(body)
            } else if (response.isSuccessful && body?.meta?.resultCount == 0) {
                Resource.Empty(body)
            } else {
                Resource.Error(response.body()?.detail!!)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    /*get spaces by ids*/
    override suspend fun getSpacesByIds(
        token: String,
        ids: String,
        spaceFields: String,
        userFields: String,
        expansions: String,
        topicFields: String
    ): Resource<SpaceListResponse> {

        return try {
            val response = twitterApiService.getSpacesByIds(token, ids, spaceFields, userFields ,expansions, topicFields)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                Resource.Success(body)
            } else if (response.isSuccessful && body?.meta?.resultCount == 0) {
                Resource.Empty(body)
            } else {
                Resource.Error(response.body()?.detail!!)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    /*get spaces by id*/
    override suspend fun getSpacesById(
        token: String,
        id: String,
        spaceFields: String,
        userFields: String,
        expansions: String,
        topicFields: String
    ): Resource<SpaceSingleResponse> {
        return try {
            val response = twitterApiService.getSpacesById(
                token,
                id,
                spaceFields,
                userFields,
                expansions,
                topicFields
            )

            val body = response.body()
            if (response.isSuccessful && body != null) {
                Resource.Success(body)
            } else if (response.isSuccessful && body?.meta?.resultCount == 0) {
                Resource.Empty(body)
            } else {
                Resource.Error(response.body()?.detail!!)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun getSpacesByCreatorIds(
        token: String,
        userIds: String,
        spaceFields: String,
        userFields: String,
        expansions: String,
        topicFields: String
    ): Resource<SpaceListResponse> {
        return try {
            val response =  twitterApiService.getSpacesByCreatorIds(token, userIds, spaceFields, userFields, expansions, topicFields)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                Resource.Success(body)
            } else if (response.isSuccessful && body?.meta?.resultCount == 0) {
                Resource.Empty(body)
            } else {
                Resource.Error(response.body()?.detail!!)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
}