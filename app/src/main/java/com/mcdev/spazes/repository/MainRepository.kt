package com.mcdev.spazes.repository

import com.mcdev.spazes.util.Resource
import com.mcdev.twitterapikit.response.SpaceResponseList

interface MainRepository {
    suspend fun getSpacesByTitle(token: String, query: String, spaceFields: String, userFields: String, expansions: String): Resource<SpaceResponseList>
}