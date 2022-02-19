package com.mcdev.spazes.repository

import com.mcdev.spazes.util.Resource
import com.mcdev.twitterapikit.response.SpaceListResponse
import com.mcdev.twitterapikit.response.SpaceSingleResponse

interface MainRepository {

    suspend fun getSpacesByTitle(
        token: String,
        query: String,
        spaceFields: String,
        userFields: String,
        expansions: String,
        topicFields: String
    ): Resource<SpaceListResponse>

    suspend fun getSpacesByIds(
        token: String,
        ids: String,
        spaceFields: String,
        userFields: String,
        expansions: String,
        topicFields: String
    ): Resource<SpaceListResponse>

    suspend fun getSpacesById(
        token: String,
        id: String,
        spaceFields: String,
        userFields: String,
        expansions: String,
        topicFields: String
    ): Resource<SpaceSingleResponse>

    suspend fun getSpacesByCreatorIds(
        token: String,
        userIds: String,
        spaceFields: String,
        userFields: String,
        expansions: String,
        topicFields: String
    ): Resource<SpaceListResponse>
}