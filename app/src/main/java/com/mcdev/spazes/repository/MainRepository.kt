package com.mcdev.spazes.repository

import com.mcdev.spazes.util.Resource
import com.mcdev.twitterapikit.response.SpaceResponseList
import com.mcdev.twitterapikit.response.SpaceResponseSingle

interface MainRepository {

    suspend fun getSpacesByTitle(
        token: String,
        query: String,
        spaceFields: String,
        userFields: String,
        expansions: String,
        topicFields: String
    ): Resource<SpaceResponseList>

    suspend fun getSpacesByIds(
        token: String,
        ids: String,
        spaceFields: String,
        userFields: String,
        expansions: String,
        topicFields: String
    ): Resource<SpaceResponseList>

    suspend fun getSpacesById(
        token: String,
        id: String,
        spaceFields: String,
        userFields: String,
        expansions: String,
        topicFields: String
    ): Resource<SpaceResponseSingle>

}