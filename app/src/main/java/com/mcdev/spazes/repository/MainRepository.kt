package com.mcdev.spazes.repository

import com.mcdev.spazes.dto.SpacesResponse
import com.mcdev.spazes.util.Resource

interface MainRepository {
    suspend fun getSpacesByTitle(token: String, query: String, spaceFields: String, userFields: String, expansions: String): Resource<SpacesResponse>
}